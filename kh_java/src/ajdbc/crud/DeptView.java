package ajdbc.crud;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import oracle.vo.DeptVO;

public class DeptView extends JFrame implements ActionListener, MouseListener {
	
	JPanel           	 jp_north      = new JPanel();            // ����Ʈ���̾ƿ�:FlowLayout
	JButton          	 jbtn_sel      = new JButton("��ȸ");
	JButton          	 jbtn_ins      = new JButton("�Է�");
	JButton         	 jbtn_upd      = new JButton("����");
	JButton          	 jbtn_del      = new JButton("����");

	String           	 cols[]        = {"�μ���ȣ", "�μ���", "����"};
	String            	 data[][]      = new String[0][3];
	DefaultTableModel    dtm           = new DefaultTableModel(data, cols);
	

	JTable            	 jtb           = new JTable(dtm);
	JScrollPane       	 jsp           = new JScrollPane(jtb);
	
	JPanel           	 jp_south      = new JPanel();            // ����Ʈ���̾ƿ�:FlowLayout
	JTextField       	 jtf_deptno    = new JTextField("", 10);
	JTextField       	 jtf_dname     = new JTextField("", 20);
	JTextField       	 jtf_loc       = new JTextField("", 20);

	DeptController		 deptCtrl 	   = new DeptController(this);

	public DeptView() {
		jbtn_sel.addActionListener(this);
	    jbtn_ins.addActionListener(this);
	    jbtn_upd.addActionListener(this);
	    jbtn_del.addActionListener(this);
	      
	    jtb.addMouseListener(this);
	      
	    initDisplay();
	}
	
	public void initDisplay() {
		jp_north.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp_north.add(jbtn_sel);
		jp_north.add(jbtn_ins);
		jp_north.add(jbtn_upd);
		jp_north.add(jbtn_del);
		jp_south.add(jtf_deptno);
		jp_south.add(jtf_dname);
		jp_south.add(jtf_loc);
		this.add("North", jp_north);
		this.add("Center", jsp);
		this.add("South", jp_south);
		this.setTitle("�μ������ý���");
		this.setSize(600, 400);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new DeptView();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int index[] = jtb.getSelectedRows();
		
		// ���̺��� �����͸� �������� ���� ���
		if(index.length == 0) {
			JOptionPane.showMessageDialog(this, "��ȸ�� �����͸� �����ϼ���.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int udeptno = 0;
		udeptno = Integer.parseInt(dtm.getValueAt(index[0], 0).toString());
		
		DeptVO pdVO = new DeptVO();
		pdVO.setCommand("select");
		pdVO.setDeptno(udeptno);
		deptCtrl.send(pdVO);
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		// �� ��ȸ �����ž�?
		if(obj == jbtn_sel) {
			System.out.println("��ü ��ȸ ȣ�� ����");
			
			deptCtrl.deptSelectAll();
		}
		
		// �Է��ϰ� �ʹ�?
		else if(obj == jbtn_ins) {
			System.out.println("�Է� ȣ�� ����");
			
			String deptno 	= getDeptno();
			String dname 	= getDname();
			String loc 		= getLoc();
			//System.out.println(deptno + ", " + dname + ", " + loc);
			
			DeptVO pdVO = new DeptVO();
			
			pdVO.setCommand("insert");
			pdVO.setDeptno(Integer.parseInt(deptno));
			pdVO.setDname(dname);
			pdVO.setLoc(loc);
			deptCtrl.send(pdVO);
		}
		// �����Ұž�?
		else if(obj == jbtn_upd) {
			System.out.println("���� ȣ�� ����");
			String deptno 	= getDeptno();
			String dname 	= getDname();
			String loc 		= getLoc();
			
			DeptVO pdVO = new DeptVO();
			
			pdVO.setCommand("insert");
			pdVO.setDeptno(Integer.parseInt(deptno));
			pdVO.setDname(dname);
			pdVO.setLoc(loc);
			deptCtrl.send(pdVO);
		}
		
		// ������ ����? : view -> action(delete) -> action(select all) -> view
		else if(obj == jbtn_del) {
			System.out.println("���� ȣ�� ����");
			
			int index[] = jtb.getSelectedRows();
			
			if(index.length == 0) {
				JOptionPane.showMessageDialog(this, "������ �����͸� �����ϼ���.....", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				Integer deptno = (Integer)dtm.getValueAt(index[0], 0);
				System.out.println("����ڰ� ������ �μ���ȣ : " + deptno);
				
				DeptVO pdVO = new DeptVO();
				
				pdVO.setCommand("delete");
				pdVO.setDeptno(deptno);
				deptCtrl.send(pdVO);
			}
		}
	}
	
	public String getDeptno() { return jtf_deptno.getText(); }
	public void setDeptno (int deptno)  { jtf_deptno.setText(String.valueOf(deptno)); }
	public String getDname () { return jtf_dname.getText(); }
	public void setDname  (String dname)   { jtf_dname.setText(dname); }
	public String getLoc   () { return jtf_loc.getText(); }
	public void setLoc    (String loc)     { jtf_loc.setText(loc); }

}
