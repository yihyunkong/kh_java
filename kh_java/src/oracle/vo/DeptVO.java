package oracle.vo;

public class DeptVO {
	// ������ ���������ڴ� private�� �Ѵ�.
	// ���ÿ� ���� ������� �����Ͽ� ���� �ٸ� ������ �����ؾ��ϰ�
	// �� ���� ������ ������ �Ǵ� ���� �����ؾ��ϱ� �����̴�.
	private int 	deptno 	= 0;	// �μ���ȣ
	private String 	dname 	= " ";	// �μ���
	private String 	loc		= " ";	// ����
	// dept ���̺� �ִ� �÷��� �ƴ����� MVC ���� �����
	// ������� ��û�� �б��ϴ� �������� �ʿ��� �����̴�.
	private String  command = "";
	// ����Ŭ ������ �Է�, ����, ���� ���� �� �����޴� �� ���
	private int		result	= 0;
	
	// ���� ������ ���� �� ���� ������ getter-setter �������Ѵ�.
	public int getDeptno() {
		return deptno;
	}
	public void setDeptno(int deptno) {
		this.deptno = deptno;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
}
