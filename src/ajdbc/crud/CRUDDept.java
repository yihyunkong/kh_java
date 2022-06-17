package ajdbc.crud;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ajdbc.dept.DBConnectionMgr;
import oracle.vo.DeptVO;

// ���� ����� ������ �����ϱ� ���ؼ� �������̽��� �����ϰ� �ִ�. 
// �������̽��� ���� ó�� �����ϴ�.
public class CRUDDept extends JFrame implements ActionListener, MouseListener {
	// �����
	/////////////////////////////// DB���� ///////////////////////////////
	DBConnectionMgr 	dbMgr 			= new DBConnectionMgr();
	
	Connection 			con 			= null; // �������
	PreparedStatement 	pstmt 			= null; // DML ���� �����ϰ� ����Ŭ���� ��û
	ResultSet 			rs 				= null; // ����Ŭ Ŀ�� ����
	/////////////////////////////// DB���� ///////////////////////////////
	
	// JFrame�� ����Ʈ ���̾ƿ��� BorderLayout
	JPanel  			jp_north 		= new JPanel();				// ����Ʈ���̾ƿ�:FlowLayout
	JButton 			jbtn_sel 		= new JButton("��ȸ");
	JButton 			jbtn_ins 		= new JButton("�Է�");
	JButton 			jbtn_upd 		= new JButton("����");
	JButton 			jbtn_del 		= new JButton("����");
	
	// ���� ���� ���迡 �ִ�. - ������ ����(�ν��Ͻ�ȭ - �̱��� ����), ��ü ���Թ�, annotation 
	String				cols[]			= {"�μ���ȣ", "�μ���", "����"};
	String				data[][]		= new String[0][3];
	DefaultTableModel 	dtm				= new DefaultTableModel(data, cols);
	JTable				jtb				= new JTable(dtm);
	JScrollPane 		jsp				= new JScrollPane(jtb);
	
	// ���̺��� �ο쿡 ���ε��ϱ� - UI solution �⺻ ����
	JPanel	   			jp_south		= new JPanel();				// ����Ʈ���̾ƿ�:FlowLayout
	JTextField 			jtf_deptno 		= new JTextField("", 10);
	JTextField 			jtf_dname 		= new JTextField("", 20);
	JTextField 			jtf_loc 		= new JTextField("", 20);
	
	// ������ - �̺�Ʈ �ҽ�, �̺�Ʈ �ڵ鷯 mapping
	// >> ���� �̺�Ʈ ó���� ����ϴ� �ڵ鷯 Ŭ�����̴�. 
	// >> ActionListner al = ne CRUDDept();
	// >> ����ο� �������� Ŭ���� �̸��� �ٸ���.
	// >> �������� ���� �� �ִ�.
	// >> Ŭ���� ������ ���յ��� ���� �� �־ ���� �׽�Ʈ�� ������ ������ �ȴ�. 
	// >> �������� �̸����� �����ȴٴ� ��.
	public CRUDDept() {
		jbtn_sel.addActionListener(this);
		jbtn_ins.addActionListener(this);
		jbtn_upd.addActionListener(this);
		jbtn_del.addActionListener(this);
		
		jtb.addMouseListener(this);
		
		initDisplay();
	}
	
	/************************************************************************
	 * �μ� ��� �����ϱ�
	 * VO(Value Object) - ����Ŭ�� Ÿ�԰� �ڹ��� Ÿ���� ���ؼ� ����ȭ�� �ϴ� ���� 
	 * 					  (�÷���� VO�� ������ Map�� key���� �ݵ�� ��ġ�ؾ��Ѵ�.)
	 * @param pdVO - ����ڰ� �Է��� �μ���ȣ, �μ���, ������ �޴´�. - ���յ����� Ŭ���� 
	 * @return int - 1: ��� ����, 0: ��� ����
	 * insert into dept(deptno, dname, loc)			
             values(71, '����1��', '������')			
	 ************************************************************************/
	// �μ��Է� �޼ҵ�
	public int deptInsert(DeptVO pdVO) {
		System.out.println("deptInsert ȣ�� ����");
		int result = 0;
		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into dept(deptno, dname, loc) values(?, ?, ?)");
        
		// ���������� �������ִ� ����Ŭ ������ ����Ѵ�. 
		// �ݵ�� ����ó���� �ؾ��Ѵ�.
		// ����� �ڿ��� �ݳ�ó�� �ؾ��Ѵ�. - ��������� ��
		// ������ �������� �ݳ��Ѵ�. �� ������ �����ΰ�?
		// >> �������迡 �ֱ� �����̴�. Connection, PreparedStatement, ResultSet - �ڹ� ���� Ʃ�� ���̵�
        try {
        	con 	= dbMgr.getConnection();
			pstmt 	= con.prepareStatement(sql.toString());
			
			// ���� ������ ó���ϴ� PreparedStatement���� ? �ڸ��� �ʿ��� �Ķ���͸� �����ϴµ�
			// ���̺� ���谡 �ٲ�ų� �÷��� �߰��Ǵ� ��츦 �����Ͽ� �ּ��� �ڵ� ������ �ǵ��� ������ ����Ѵ�.
			// ? �ڸ��� 1�����̹Ƿ� ++i�� �����Ѵ�. 
			// ���� 1�� �ʱ�ȭ �ߴ�.�� i++�� �����ؾ��Ѵ�.
			int i = 0; // ������ �����ؼ� �߰��� ���� ������ �÷��� ���� �Ǵ��� ������ �ʿ䰡 ���� ! 
			
			pstmt.setInt(++i, pdVO.getDeptno());
			pstmt.setString(++i, pdVO.getDname());
			pstmt.setString(++i, pdVO.getLoc());
			
			// select�� ��� Ŀ���� ���� �ް�, insert, update, delete�� ��� int�� ���� �޴´�. 
			result = pstmt.executeUpdate();
			
			// ����Ŭ �������� �Է� ó���� �������� ��, 1�� ���� �޴´�.
			if(result == 1) {
				deptSelectAll();
				// �Է� ���� �Ŀ� ȭ�鿡 ���� �ʱ�ȭ - ������� ���Ǽ� ����
				setDeptno	(0);
				setDname	("");
				setLoc		("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(pstmt, con);
		}
		return result;
	}
	
	/************************************************************************
	 * �μ� ���� �����ϱ�
	 * @param pdVO - ����ڰ� �Է��� �μ���ȣ, �μ���, ������ �޴´�. - ���յ����� Ŭ���� 
	 * @return int - 1: ��� ����, 0: ��� ����
	 * update dept		
     	  set dname = '����2��'		
             ,loc   = '������'		
        where deptno = 71
	 ************************************************************************/
	// �μ����� �޼ҵ�
	public int deptUpdate(DeptVO pdVO) {
		System.out.println("deptUpdate ȣ�� ����");
		
		StringBuilder sql = new StringBuilder();
		sql.append("update dept		   ");
	   	sql.append("   set dname = ?   ");
	    sql.append("      ,loc   = ?   ");		
	    sql.append(" where deptno = ?  ");
	    
		int result = 0;
		
		try {
			con = dbMgr.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			
			int i = 1;
			
			pstmt.setString(i++, pdVO.getDname());
			pstmt.setString(i++, pdVO.getLoc());
			pstmt.setInt(i++, pdVO.getDeptno());
			
			result = pstmt.executeUpdate();
			
			if(result == 1) {
				JOptionPane.showMessageDialog(this, "�����Ͱ� �����Ǿ����ϴ�.", "Info", JOptionPane.INFORMATION_MESSAGE);
				deptSelectAll();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
		
		return result;
	}
	
	/************************************************************************
	 * �μ� ���� �����ϱ�
	 * @param deptno(int) - ����ڰ� ������ �μ���ȣ, �μ���, ������ �޴´�. - ���յ����� Ŭ���� 
	 * @return int - 1: ��� ����, 0: ��� ����
	 * delete from dept	
		where deptno = 71
	 ************************************************************************/
	// �μ����� �޼ҵ�
	public int deptDelete (int deptno) { // �Ķ���� ������ pk�� �ޱ�
		System.out.println("deptUpdate ȣ�� ���� : " + deptno);
		int result = 0;
		
		StringBuilder sql = new StringBuilder();
		sql.append("delete from dept  ");
		sql.append(" where deptno = ? ");
		
		try {
			con 	= dbMgr.getConnection();
			pstmt 	= con.prepareStatement(sql.toString());
			pstmt.setInt(1, deptno);
			
			result = pstmt.executeUpdate();
			
			if(result == 1) {
				JOptionPane.showMessageDialog(this, "�����Ͱ� �����Ǿ����ϴ�.", "Info", JOptionPane.INFORMATION_MESSAGE);
				// ������ �Ŀ� ȭ�� ���� ó���ϱ� - ����ȭ ó�� �����
				// �Է�, ����, �������� �ݺ������� ȣ��� �� �ִ�.
				// List<VO>, List<Map>
				deptSelectAll(); // ���ΰ�ħ ó�� �޼ҵ� ȣ���ϱ� - �޼ҵ� ���뼺 - �ݺ��Ǵ� �ڵ带 �ٿ��ش�.
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(pstmt, con);
		}
		return result;
	}
	
	/************************************************************************
	 * �μ���� ��ü ��ȸ ���� (���ΰ�ħ �� ���� ���ؼ�)
	 * @return List<Map<String, Object>>
	 * select deotno, dname, loc from dept
	 ***********************************************************************/
	// ��ü��ȸ �޼ҵ�
	public List<Map<String, Object>> deptSelectAll() {
		System.out.println("deptSelectAll ȣ�� ����");
		List<Map<String, Object>> deptList = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select deptno, dname, loc from dept");
		
		try {
			con 	= dbMgr.getConnection();				// ����
			pstmt 	= con.prepareStatement(sql.toString()); // ��������
			rs		= pstmt.executeQuery();					// Ŀ��
			
			Map<String, Object> rmap = null;
			
			while(rs.next()) {
				rmap = new HashMap<>(); // ���� �̸��� ����������, ���� �ٸ� �ּҹ����� ���´�. 
				
				rmap.put("deptno", rs.getInt("deptno"));
				rmap.put("dname", rs.getString("dname"));
				rmap.put("loc", rs.getString("loc"));
				
				deptList.add(rmap); // ������ ��������. �⺻������ ����Ŭ���� �ϴ� ���� ������. (order by, index)
			}
//			System.out.println(deptList);
			
			// ������ ��ȸ�� ���, �� ����� �����ϱ�. 
			while(dtm.getRowCount() > 0) {
				// �Ķ���Ϳ� 0�� �־ ���̺��� �ε����� �ٲ�� ������ �ذ��Ѵ�.
				dtm.removeRow(0);
			}
			
			// Iterator�� �ڷᱸ���� �����ִ� �����l ������ üũ�ϴµ� �ʿ��� �޼ҵ带 �����ϰ� �ִ�.
			Iterator<Map<String, Object>> iter = deptList.iterator();
			
			Object keys[] = null;
			
			while(iter.hasNext()) {
				Map<String, Object> data = iter.next();
				keys = data.keySet().toArray();
				Vector<Object> oneRow = new Vector<>();
				oneRow.add(data.get(keys[2]));
				oneRow.add(data.get(keys[1]));
				oneRow.add(data.get(keys[0]));
				// �����ͼ��� DefaultTableModel�� ��ȸ ��� ��� - �ݺ�ó���� => 10, 20, 30, 40
				dtm.addRow(oneRow);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
		return deptList;
	}
	
	/************************************************************************
	 * �μ���� �� ��ȸ ����
	 * @param  deptno(int)
	 * @return DeptVO
	 * selelct deptno, dname, loc from dept
		 where deptno = ?
	 ***********************************************************************/
	// ����ȸ �޼ҵ�
	public DeptVO deptSelectDetail(int deptno) {
		System.out.println("deptSelectDetail ȣ�� ����");
		
		StringBuilder sql = new StringBuilder();
		sql.append("select deptno, dname, loc from dept");
		sql.append(" where deptno = ?                   ");
				 
		DeptVO rdVO = null;
		
		try {
			con = dbMgr.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setInt(1, deptno);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				rdVO = new DeptVO();
				rdVO.setDeptno(rs.getInt("deptno"));
				rdVO.setDname(rs.getString("dname"));
				rdVO.setLoc(rs.getString("loc"));
			}
			
			if(rdVO != null) {
				setDeptno(rdVO.getDeptno());
				setDname(rdVO.getDname());
				setLoc(rdVO.getLoc());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
		
		return rdVO;
	}
	
	// ȭ��ó����
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
	
	// ���θ޼ҵ�
	public static void main(String[] args) {
		new CRUDDept();
	}

	// �߻�޼ҵ�
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		// �� ��ȸ �����ž�?
		if(obj == jbtn_sel) {
			System.out.println("��ü ��ȸ ȣ�� ����");
			
			deptSelectAll();
		}
		
		// �Է��ϰ� �ʹ�?
		else if(obj == jbtn_ins) {
			System.out.println("�Է� ȣ�� ����");
			
			String deptno 	= getDeptno();
			String dname 	= getDname();
			String loc 		= getLoc();
			//System.out.println(deptno + ", " + dname + ", " + loc);
			
			DeptVO pdVO = new DeptVO();
			pdVO.setDeptno(Integer.parseInt(deptno));
			pdVO.setDname(dname);
			pdVO.setLoc(loc);
			deptInsert(pdVO);
		}
		// �����Ұž�?
		else if(obj == jbtn_upd) {
			System.out.println("���� ȣ�� ����");
			String deptno 	= getDeptno();
			String dname 	= getDname();
			String loc 		= getLoc();
			
			DeptVO pdVO = new DeptVO();
			pdVO.setDeptno(Integer.parseInt(deptno));
			pdVO.setDname(dname);
			pdVO.setLoc(loc);
			deptUpdate(pdVO);
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
				deptDelete(deptno);
			}
		}
	} /////////////////////////////////////////////////////////// end of actionPerformed
	
	// �� �÷��� ������ �����ϰų� �о���� getter-setter �޼ҵ�
	public String getDeptno() { return jtf_deptno.getText(); }
	public void setDeptno (int deptno)  { jtf_deptno.setText(String.valueOf(deptno)); }
	public String getDname () { return jtf_dname.getText(); }
	public void setDname  (String dname)   { jtf_dname.setText(dname); }
	public String getLoc   () { return jtf_loc.getText(); }
	public void setLoc    (String loc)     { jtf_loc.setText(loc); }

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
		deptSelectDetail(udeptno);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
	
}
