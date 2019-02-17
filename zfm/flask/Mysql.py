import MySQLdb,sys,logging


def printlog():
	logging.basicConfig(level=logging.DEBUG,
		    filename='./sqllog.txt',
                    filemode='a',
                    format='%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s')


def Mysqlconnidatabase(cmd=None,param=None):
        try:
                conn = MySQLdb.connect("192.168.30.166","test","User_Tjj_NG8Pzxgs_NT",charset="utf8")
        except MySQLdb.OperationalError, message:
                print "link error"
                sys.exit(1)
        cursor=conn.cursor()
        cursor.execute('SHOW DATABASES')
        data=cursor.fetchall()
        cursor.close()
        conn.close()
	res = [i[0] for i in data]
	print(res)	
        return res





def Mysqlconn(databases="",cmd=None,param=None): 
	try:
		conn = MySQLdb.connect("192.168.30.166","test","User_Tjj_NG8Pzxgs_NT",databases,charset="utf8")
	except MySQLdb.OperationalError, message:
		print "link error"
		sys.exit(1)
	cursor=conn.cursor()
	cursor.execute(cmd,param)
	conn.commit()
	data=cursor.fetchall()
	dataziduan = cursor.description
	cursor.close()
	conn.close()
	print(data)
	b = [[i[0] for i in dataziduan],data]
	print(b)
	return b

if __name__ == '__main__':
	Mysqlconnidatabase()
	#Mysqlconn()
