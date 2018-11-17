package.path = '/usr/local/nginx/?.lua;' .. package.path

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

	local args = ngx.req.get_uri_args()
	local sign = args["sign"]
	local module = args["m"] or args["M"]
	local action = args["a"] or args["A"]
	local user_id = args["user_id"]
	local user_token = args["token"]

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

