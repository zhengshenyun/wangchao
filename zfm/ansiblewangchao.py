# vim exec_ansible.py
#encoding=utf8
from ansible.inventory.manager import InventoryManager
from ansible.playbook.play import Play
from ansible.executor.task_queue_manager import TaskQueueManager
from ansible.plugins.callback import CallbackBase
from collections import namedtuple 
from ansible.parsing.dataloader import DataLoader
from ansible.vars.manager import VariableManager
import json

class ResultCallback(CallbackBase):
    """A sample callback plugin used for performing an action as results come in
 
    If you want to collect all results into a single object for processing at
    the end of the execution, look into utilizing the ``json`` callback plugin
    or writing your own custom callback plugin
    """
    def v2_runner_on_ok(self, result, **kwargs):
        """Print a json representation of the result
        This method could store the result in an instance attribute for retrieval later
        """
        global exec_result
        host = result._host
        self.data = json.dumps({host.name: result._result}, indent=4)
        exec_result = dict(exec_result,**json.loads(self.data))
 
    def v2_runner_on_failed(self, result, ignore_errors=False):
        global exec_result
        host = result._host
        self.data = json.dumps({host.name: result._result}, indent=4)
        exec_result = dict(exec_result,**json.loads(self.data))
 
def exec_ansible(module,args,host):               
    Options = namedtuple('Options', ['connection', 'module_path', 'forks', 'become', 'become_method', 'become_user', 'check', 'diff'])
    # initialize needed objects
    loader = DataLoader()
    options = Options(connection='ssh', module_path='/usr/lib/python2.7/site-packages/ansible/modules/', forks=100, become=None, become_method=None, become_user=None, check=False,diff=False)
    passwords = dict(vault_pass='')
 
    # Instantiate our ResultCallback for handling results as they come in
    results_callback = ResultCallback()
 
    # create inventory and pass to var manager
    inventory = InventoryManager(loader=loader, sources=['/etc/ansible/hosts'])
    variable_manager = VariableManager(loader=loader, inventory=inventory)
 
    # create play with tasks
    play_source =  dict(
            name = "Ansible Play",
            hosts = host,
            gather_facts = 'no',
            tasks = [
                dict(action=dict(module=module, args=args), register='shell_out'),
             ]
        )
    play = Play().load(play_source, variable_manager=variable_manager, loader=loader)
 
    # actually run it
    tqm = None
    global exec_result
    try:
        tqm = TaskQueueManager(
                  inventory=inventory,
                  variable_manager=variable_manager,
                  loader=loader,
                  options=options,
                  passwords=passwords,
                  stdout_callback=results_callback,  # Use our custom callback instead of the ``default`` callback plugin
              )
        result = tqm.run(play)
	print("最后一条命令返回值为 %s\n" %result) #######################成功返回0  失败则返回其他数字
    finally:
        if tqm is not None:
            tqm.cleanup()
        return exec_result

if __name__ == '__main__':
	exec_result = {}
	a = exec_ansible("shell","free;ls","daye")  ####这边最后一个参数 填写ip地址或者是hosts里面的[]命名都是可以的
	for i in a.keys():
		if not a[i]["stderr"]:
			print("%s -----------------------------------------------SUCCESS\n %s\n %s\n\n" %(i,a[i]["stdout"],a[i]["stderr"]))
		else:
			print("%s -----------------------------------------------ERROR\n %s\n %s\n\n" %(i,a[i]["stderr"],a[i]["stdout"]))
