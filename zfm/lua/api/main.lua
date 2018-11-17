package.path = '/usr/local/nginx/?.lua;' .. package.path

local args = ngx.req.get_uri_args()
-- 该命令主要是定义一块名为lualimit的共享内存空间，内存大小为size。通过该命令定义的共享内存对象对于Nginx中所有worker进程都是可见的，当Nginx通过reload命令重启时，共享内存字典项会从新获取它的内容，当时当Nginx退出时，字典项的值将会丢失 
-- local 后面的lualimit  和ngx.shared.xxxxx 是一样的  这是规则
local lualimit = ngx.shared.lualimit
-- 是否开启封禁
local ccDeny = true
-- 封禁时间
local denyTime = 5 
-- 单URL最大请求次数
--local maxRequestByPerUri = 60000 
local maxRequestByPerUri = -1 
-- 单用户最大请求次数
local maxRequestByPerUser = 300 
-- 单签名最大请求次数
local maxRequestByPerSign = 60
-- 单token最大请求次数
local maxRequestByPerToken = 60
-- 统计周期
local ccSeconds = 60

-- 0 默认按照url拦截  1 按照签名拦截 2 按照user_id拦截 3 按照token拦截
function denyCC(denyByType)
    if ccDeny then
	
	local sign = args["sign"]
	local module = args["m"] or args["M"]
	local action = args["a"] or args["A"]
	local user_id = args["user_id"]
	local user_token = args["token"]
	-- ngx.var.uri 显示得是/ddddd/fff/ggg/hhh  不包括?和后面的
	local defaultToken = ngx.var.uri
	local token = nil 
	local ccCount = -1


	if defaultToken then
		defaulToken = string.lower(defaultToken)
		defaultToken = string.gsub(defaultToken, "/api.php", "", 1)
		defaultToken = string.gsub(defaultToken, "/t", "", 1)
		--ngx.log(ngx.ERR, "defualt token: ", defaultToken)
	end

	
	if module then

		if type(module) == 'table' then
			module = module[0]
		end
		token = module
	end

	if  action then

		if type(action) == 'table' then
			action = action[0]
		end

		if token then
			token = token .. action
		else
			token = action
		end
	end 

	-- 某个签名请求太多次，拦截
	if denyByType == 1 and sign then
		ccCount = maxRequestByPerSign
		token = sign 
		token = "sign/" .. string.lower(token)
	end

	-- 如果开启了按照用户拦截，一个用户请求次数太多， 拦截
	if denyByType == 2 and user_id then
		ccCount = maxRequestByPerUser
		token = user_id
		token = "user/" .. token
	end

	-- 如果开启了按照token拦截，同一个module/action的token请求次数太多，拦截
	if denyByType == 3 and user_token and token then
		ccCount = maxRequestByPerToken
		token = token .. user_token 
		token = "token/" .. token
	end

	-- 默认按照url拦截
	if denyByType == 0 and defaultToken  then
		ccCount = maxRequestByPerUri
		token = defaultToken
		if token == "" or token == "/" then
			token = nil
		else
			token = "uri/" .. token
		end
	end

	if token == nil or token == "" then
		return false
	end

	-- 如果不拦截某个指标则把当前指标设置为小于0点数
	if ccCount <= 0 then
		return false
	end

        local limit = lualimit

	local isDeny, _ = limit:get(denyKey)
        local req, _ = limit:get(token)

	local denyKey = token .. "/deny"

	local isDeny, _ = limit:get(denyKey)

	if isDeny then
		return true
	end


        if req then
            if req > ccCount then
            	limit:safe_set(denyKey, 1, denyTime)
		limit:delete(token)
		ngx.log(ngx.ERR, "deny type: ", denyKey)
                return true
            else
                 limit:incr(token,1)
            end
        else
            limit:safe_set(token,1,ccSeconds)
        end
    end
    return false
end


function checkCC() 
	if denyCC(0) or  denyCC(1) or denyCC(2) or denyCC(3) then
		ngx.exit(503)
	end
end

checkCC()

-- auth ---

local md5
if ngx == nil or ngx.md5 == nil then
	md5 = require('lua.api.md5')
	md5 = md5.sumhexa
else
	md5 = ngx.md5
end

local config = require('lua.api.config')

local function check_module_exists(module_name) 
	if module_name == nil then return false end

	for _, m in pairs(config['auth_modules'])
	do
		if string.lower(m) == string.lower(module_name) then
			return true
		end
	end	
	return false
end

local function ngx_say(str)
		ngx.header["Content-Type"] = "text/html;charset=utf-8"
		if ngx.headers_sent ~= true then
			ngx.send_headers()
		end
		ngx.print(str)
end



--local args = ngx.req.get_uri_args()
local module_key = config['module_key'];
local module_name = args[module_key]
local auth_secret = config['auth_secret']
local auth_sign_key = config['auth_sign_key']
local auth_timestamp_key  = config['auth_timestamp_key']
local auth_timeout = config['auth_timeout']


local function check_sign() 
	if type(args) ~= 'table' then return false end

	local sign = args[auth_sign_key]
	if sign == nil then return false end
	args[auth_sign_key] = nil

	local args_keys  = {}
	for k, v in pairs(args) do
		table.insert(args_keys, k)
	end
	table.sort(args_keys)
	local s = ''
	
	for _, k in pairs(args_keys) do
		if type(args[k]) ~= 'string' then
            if type(args[k]) == 'table' then
                for _, v in pairs(args[k]) do
                    s = s .. "" .. tostring(v)
                end
                args[k] = nil
            else
			    args[k] = tostring(args[k])
            end
		end
        if args[k] ~= nil then
		    s = s .. "" .. args[k]
        end
	end

	s = md5(auth_secret .. md5(s))
	if s ~= sign then return false end
	return true
end



function auth()
	if check_module_exists(module_name) == true then
		-- must https,  now use http just for test
	--	if ngx.var.scheme ~= 'https' then 
	--		ngx.exit(ngx.HTTP_UNAUTHORIZED)
	--	end

		if check_sign() ~= true then
			ngx.exit(ngx.HTTP_UNAUTHORIZED)
		--else
			-- check timestamp 
		--	local t = args[auth_timestamp_key]
		--	if (t + auth_timeout) < ngx.time() then
		--		ngx.exit(ngx.HTTP_UNAUTHORIZED)
		--	end
		end
	end
end

auth()

