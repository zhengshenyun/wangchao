package.path = '/usr/local/tengine/?.lua;' .. package.path

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



local args = ngx.req.get_uri_args()

local module_key = config['module_key'];
local module_name = args[module_key]
local auth_secret = config['auth_secret']
local auth_sign_key = config['auth_sign_key']
local auth_timestamp_key  = config['auth_timestamp_key']
local auth_timeout = config['auth_timeout']


local function check_sign(args) 
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
	--if check_module_exists(module_name) == true then
		-- must https,  now use http just for test
		--if ngx.var.scheme ~= 'https' then 
		--	ngx.exit(ngx.HTTP_UNAUTHORIZED)
		--end

		if check_sign(args) ~= true then
			ngx.exit(ngx.HTTP_UNAUTHORIZED)
		else
			-- check timestamp 
			local t = args[auth_timestamp_key]
			if (t + auth_timeout) < ngx.time() then
				ngx.exit(ngx.HTTP_UNAUTHORIZED)
			end
		end
	--end
end

return auth
