package ajdbc.member;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ajdbc.zipcode.DBConnectionMgr;

public class ZipcodeSearch extends JFrame implements FocusListener, ActionListener, MouseListener {
	// �����
	/////////////////////////////// DB���� ///////////////////////////////
	DBConnectionMgr    	dbMgr          = new DBConnectionMgr();
	
	Connection          con            = null; // �������
	PreparedStatement   pstmt          = null; // DML ���� �����ϰ� ����Ŭ���� ��û
	ResultSet          	rs             = null; // ����Ŭ Ŀ�� ����
	/////////////////////////////// DB���� ///////////////////////////////

	JPanel jp_north = new JPanel();
	JTextField jtf_dong = new JTextField("�� �̸��� �Է��ϼ���.", 20);
	JButton jbtn_search = new JButton("ã��");
	
	String zdos[] = {"��ü", "����", "���"};
	JComboBox jcb = new JComboBox(zdos);
	
	String cols[] 	= {"�����ȣ", "�ּ�"}; // JTable Header�� �� 1�� �迭
	String data[][] = new String[0][2];	// Body �κ� data�� �� 2�� �迭 
	// 0�̿��� �������� - ��������.
	DefaultTableModel dtm  = new DefaultTableModel(data, cols);
	
	JTable			  jtb  = new JTable(dtm);
	
	Font			  font = new Font("����ü", Font.BOLD, 18);
	JScrollPane		  jsp  = new JScrollPane(jtb);
	
	MemberShip		  ms   = null;
	
	// �ļ���
	public ZipcodeSearch() {
		
	}
	
	// memeberShip Ŭ������ ����ϱ� ���ؼ� �Ķ���� ���� ���� !
	public ZipcodeSearch(MemberShip ms) {
		this.ms = ms;
	}
	
	// ȭ��ó����
	public void initDisplay() {
		jtb.addMouseListener(this);
		
		jtf_dong.addFocusListener(this);
		jtf_dong.addActionListener(this);
		jbtn_search.addActionListener(this);
		
		jp_north.setLayout(new BorderLayout());
		
		jp_north.add("West", jcb);
		jp_north.add("Center", jtf_dong);
		jp_north.add("East", jbtn_search);
		
		this.add("North", jp_north);
		this.add("Center", jsp);
		this.setTitle("�����ȣ�˻���"); // â�� �̸�
		this.setSize(430, 400); // â ������
		this.setVisible(true); // â�� ���̵��� true
	}
	
	//
	public void refreshData(String dong) {
		List<Map<String, Object>> zipList = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select                              	");
        sql.append("	   zipcode, address                 ");
        sql.append("  from zipcode_t                    	");
        sql.append(" where dong like '%' || ? || '%'		");
		
		try {
			con 	= dbMgr.getConnection(); // ����Ŭ ������ �����ϱ�
			pstmt 	= con.prepareStatement(sql.toString()); // ����Ŭ ������ sql�� �����ϱ�
			pstmt.setString(1, dong); // ����ڰ� �Է��� �� ������ �Ķ���ͷ� �޾Ƽ� ?�� ġȯ�ȴ�.
			rs		= pstmt.executeQuery(); // select���� ���� Ŀ���� table�� ���� Ȯ���ϱ�
			
			Map<String, Object> rmap = null;
			
			while(rs.next()) {
				rmap = new HashMap<>();
				rmap.put("zipcode", rs.getString("zipcode"));
				rmap.put("address", rs.getString("address"));
				zipList.add(rmap);
			}
			
			// ��ȸ�� ����� defaultTableModel�� mapping �ϱ�
			for(int i=0; i<zipList.size(); i++) {
				Map<String, Object> map = zipList.get(i);
				
				Vector<Object> oneRow = new Vector<>();
				oneRow.add(0, map.get("zipcode"));
				oneRow.add(1, map.get("address"));
				dtm.addRow(oneRow);
			}
		} catch (SQLException se) {
			se.printStackTrace();
			System.out.println(se.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
	}
	
	// ���� �޼ҵ�
	public static void main(String[] args) {
		ZipcodeSearch zc = new ZipcodeSearch();
		zc.initDisplay();
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == jtf_dong) {
			jtf_dong.setText("");
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj == jtf_dong || obj == jbtn_search) {
			String user = jtf_dong.getText(); // ����, ���, ...
			refreshData(user);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getClickCount() == 2) {
			System.out.println("����Ŭ���Ѱž�?");
		}
		
		int index[] = jtb.getSelectedRows();
		
		if(index.length == 0) {
			JOptionPane.showMessageDialog(this, "��ȸ�� �����͸� �����ϼ���.");
			return;
		} else {
			// ����ڰ� ����Ŭ���� ������ �����ȣ ��������
			String zipcode = (String)dtm.getValueAt(index[0], 0);
			// ����ڰ� ����Ŭ���� �ο��� �ּ� ��������
			String address = (String)dtm.getValueAt(index[0], 1);
//			System.out.println(zipcode + ", " + address);
			ms.jtf_zipcode.setText(zipcode);
			ms.jtf_address.setText(address);
		}
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

}
