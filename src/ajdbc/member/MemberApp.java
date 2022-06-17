package ajdbc.member;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ajdbc.zipcode.DBConnectionMgr;

// ȭ�� ������ JFrame
// JFrame jf = new JFrame();
// jf.setTitle("�ڹٽǽ�");
// JFrame jf = new JFrame();

public class MemberApp extends JFrame implements ActionListener, MouseListener {
	/////////////////////////////// DB���� ///////////////////////////////
	DBConnectionMgr    	dbMgr       = new DBConnectionMgr();
	
	Connection          con         = null; // �������
	PreparedStatement   pstmt       = null; // DML ���� �����ϰ� ����Ŭ���� ��û
	ResultSet          	rs          = null; // ����Ŭ Ŀ�� ����
	/////////////////////////////// DB���� ///////////////////////////////
	
	// ����
	JPanel            jp_north      = new JPanel();
	
	// ��ư
	JButton           jbtn_sel      = new JButton("��ȸ");
	JButton           jbtn_ins      = new JButton("�Է�");
	JButton        	  jbtn_upd      = new JButton("����");
	JButton           jbtn_del      = new JButton("����");
	
	String 			  cols[] 		= {"��ȣ", "���̵�", "�̸�", "�ּ�"}; // JTable Header�� �� 1�� �迭
	String 			  data[][] 		= new String[0][4];				// Body �κ� data�� �� 2�� �迭 
	// 0�̿��� �������� - ��������.
	DefaultTableModel dtm  			= new DefaultTableModel(data, cols);
	
	JTable			  jtb  = 		new JTable(dtm);
	
	Font			  font =	 	new Font("����ü", Font.BOLD, 18);
	JScrollPane		  jsp  = 		new JScrollPane(jtb);
		
	// ������ �ȿ��� initDisplay() : �޼ҵ尡 ȣ��Ǵ� ��ġ�� �����غ���
	MemberShip 		  ms   =		new MemberShip(this); // this�� default�� ���̸� �˾ƺ��� !
	
	// ������
	public MemberApp() {
		// �̺�Ʈ �ҽ��� �̺�Ʈ ó�� Ŭ������ ����
		jbtn_sel.addActionListener(this);
	    jbtn_ins.addActionListener(this);
	    jbtn_upd.addActionListener(this);
	    jbtn_del.addActionListener(this);
	    
		initDisplay();
		refreshData();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource(); // �̺�Ʈ�� �߻��ϴ� �̺�Ʈ �ҽ��� �ּҹ����� �����´�.
	
		if(obj == jbtn_ins) {
			ms.initDisplay();
			System.out.println("��ü ��ȸ ȣ�� ����");
		} else if(obj == jbtn_sel) {
			refreshData();
		}
	}
	
	public void refreshData() {
		List<Map<String, Object>> memList = new ArrayList<>(); // ����Ʈ�� ������ �־ ������. ���� �ӵ��� �������� ������ ����.
	
		// sql�� �����ϱ�
		StringBuilder sql = new StringBuilder();
		sql.append("select mem_no, mem_id, mem_pw, mem_name, mem_zipcode, mem_address ");
		sql.append("  from member                                                   ");
		sql.append("order by mem_no desc                                            ");
		
		try {
			con 	= dbMgr.getConnection(); // ����Ŭ ������ �����ϱ�
			pstmt 	= con.prepareStatement(sql.toString()); // ����Ŭ ������ sql�� �����ϱ�
			rs		= pstmt.executeQuery(); // select���� ���� Ŀ���� table�� ���� Ȯ���ϱ�
			
			Map<String, Object> rmap = null; // ������ ���� rmap�� Map(�ӵ��� �������� ������ ����)���� �����ϱ�
			
			while(rs.next()) {
				rmap = new HashMap<>(); // HashMap�� ���� rmap�� �� ���
				//rmap�� ���� ������ !
				rmap.put("mem_no", rs.getInt("mem_no"));
				rmap.put("mem_id", rs.getString("mem_id"));
				rmap.put("mem_name", rs.getString("mem_name"));
				rmap.put("mem_address", rs.getString("mem_address"));
				
				memList.add(rmap); // memList�� rmap �߰��ϱ�
			}
//			System.out.println(memList);
			
			// ������ ��ȸ�� ���, �� �ٽ� ��ȸ ��ư�� ������ �����ִ� ��� �����ϱ� >> ���Ӱ� ��� ��ȸ
			// �����͸� ������ �ִ°� defaultTableModel (dtm)
			while(dtm.getRowCount() > 0) { // 0 ���� ũ�� ���̺�ȿ� ������ �����ִ�.
				dtm.removeRow(0);
			}
			
			// iterator�� �ڷᱸ���� �����ִ� ������ ������ üũ�ϴµ� �ʿ��� �޼ҵ带 �����ϰ� �ִ�.
			Iterator<Map<String, Object>> iter = memList.iterator();			
			
			// ���⼭���� �𸣰ھ��
			// keys[]��� �迭�� �����͸� ��� ���ؼ�?
			Object keys[] = null; // 
			
			while(iter.hasNext()) {
				Map<String, Object> data = iter.next();
				keys = data.keySet().toArray();
				
				Vector<Object> oneRow = new Vector<>();
				oneRow.add(data.get(keys[2]));
				oneRow.add(data.get(keys[1]));
				oneRow.add(data.get(keys[0]));
				oneRow.add(data.get(keys[3]));
				dtm.addRow(oneRow);
			}
		} catch (SQLException se) {
			System.out.println("[[query]]" + sql.toString());
			System.out.println(se.toString());
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con); // ����� �ڿ� �ݳ��ϱ� (������ ��������)
		}
		
	}
	
	// ȭ�� �����
	public void initDisplay() {
		jp_north.setLayout(new FlowLayout(FlowLayout.LEFT)); // ��ư�� ���ʿ� ��ĥ�Ұž�
		
		jbtn_sel.setBackground(new Color(158, 9, 9)); // ����
		jbtn_sel.setForeground(new Color(212, 212, 212)); // ���ڻ�
		jp_north.add(jbtn_sel); // ��ȸ��ư
		
		jbtn_ins.setBackground(new Color(7, 84, 170));
		jbtn_ins.setForeground(new Color(212, 212, 212));
		jp_north.add(jbtn_ins); // �Է¹�ư
		
		jbtn_upd.setBackground(new Color(19, 99, 57));
		jbtn_upd.setForeground(new Color(212, 212, 212));
		jp_north.add(jbtn_upd); // ������ư
		
		jbtn_del.setBackground(new Color(54, 54, 54));
		jbtn_del.setForeground(new Color(212, 212, 212));
		jp_north.add(jbtn_del); // ������ư
		
		this.add("North", jp_north); // ���ʿ� jp_north��� ������ ��ư�� add �Ұž�
		
		this.add("Center", jsp);
		this.setTitle("ȸ�������ý��� ver1.0"); // â�� �̸�
		this.setSize(600, 400); // â ������
		this.setVisible(true); // â�� ���̵��� true
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}


	// ���θ޼ҵ�
	public static void main(String[] args) {
		new MemberApp();
	}

}
