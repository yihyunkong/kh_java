package ajdbc.member;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ajdbc.dept.DBConnectionMgr;

public class MemberShip extends JFrame implements ActionListener, MouseListener {
	// �����
	///////////////////////////////// DB���� /////////////////////////////////
	DBConnectionMgr 	dbMgr 	= new DBConnectionMgr();
	Connection 			con 	= null; // �������
	PreparedStatement 	pstmt 	= null; // DML���� �����ϰ� ����Ŭ���� ��û
	ResultSet 			rs		= null; // ��ȸ��� Ŀ���� ���� �ʿ�
	///////////////////////////////// DB���� /////////////////////////////////
	
	JPanel      jp_center		= new JPanel();
	JLabel 		jlb_id 			= new JLabel("���̵� ");
	JTextField 	jtf_id 			= new JTextField("", 20);
	JButton		jbtn_idcheck 	= new JButton("ID�ߺ�üũ");
	JLabel 		jlb_pw 			= new JLabel("��й�ȣ ");
	JTextField 	jtf_pw 			= new JTextField("", 20);
	JLabel 		jlb_name 		= new JLabel("�̸� ");
	JTextField 	jtf_name 		= new JTextField("", 20);
	JLabel 		jlb_zipcode 	= new JLabel("�����ȣ ");
	JTextField 	jtf_zipcode		= new JTextField("", 20);
	JButton		jbtn_zipcode 	= new JButton("�����ȣã��");
	JLabel 		jlb_address 	= new JLabel("�ּ� ");
	JTextField 	jtf_address 	= new JTextField("", 35);	
	JScrollPane jsp				= new JScrollPane(jp_center);
	
	JPanel 		jp_south		= new JPanel();
	JButton		jbtn_signup		= new JButton("ȸ������");
	JButton		jbtn_cancel		= new JButton("���");
	
	MemberApp	memberApp 		= null;
	
	// ������
	public MemberShip() {
//		initDisplay();
	}
	
	public MemberShip(MemberApp memberApp) {
		this.memberApp = memberApp;
	}
	
	public int memberInsert(MemberVO pmVO) {
		int result = 0;
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO member(mem_no, mem_id, mem_pw, mem_name, mem_zipcode, mem_address)");
		sql.append("			VALUES(seq_member_no.nextval, ?, ?, ?, ?, ?						 )");
		
		try {
			con = dbMgr.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			
			int i = 0;
			pstmt.setString(++i, pmVO.getMem_id());
			pstmt.setString(++i, pmVO.getMem_pw());
			pstmt.setString(++i, pmVO.getMem_name());
			pstmt.setString(++i, pmVO.getMem_zipcode());
			pstmt.setString(++i, pmVO.getMem_address());
			
			result = pstmt.executeUpdate();
			
			System.out.println("result : "+result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(pstmt, con);
		}
		return result;
	}
	
	/*************************************************************************
	 * ���̵� �ߺ�üũ �����ϱ�
	 * @param ����ڰ� �Է��� ���̵�
	 * @return boolean
	 * ���� boolean�� �����ߴٸ� false�̸� ����� �� ����.  true�̸� ����� �� �ִ�.
	 * ���� boolean�� �����ߴٸ� false�̸� ����� �� �ִ�.  true�̸� ����� �� ����.
	SELECT 1
	  FROM dual
	 WHERE EXISTS (SELECT mem_name
	                 FROM member
	                WHERE mem_id ='tomato') 
	 ���� 1: tomato�� �����ϴµ� false�� ��ϴ�. ���� �����ΰ���?
	 ���� 2: java.sql.SQLException: �ε������� ������ IN �Ǵ� OUT �Ű�����:: 1 �϶�
	        ?�ڸ��� �� ���� ġȯ���� ���� ���
	 ���� 3: ��忡�� ����� ������ �״�� ����� ��� �ݵ�� ?�� �ٲپ� �� ��.
	 ���� 4: java.sql.SQLSyntaxErrorException: ORA-00911: ���ڰ� �������մϴ�
	       ������ �ڿ� �����ݷ��� ���� ��쿡 �߻��ϴ� ���� �Դϴ�.                       
	*************************************************************************/   
	// ���̵� �ߺ�üũ �޼ҵ� 
	public boolean idCheck(String user_id) {
		boolean isOk = false;
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1                               ");
		sql.append("  FROM dual                            ");
		sql.append(" WHERE EXISTS (SELECT mem_name         ");
		sql.append("                 FROM member           ");
		sql.append("                WHERE mem_id = ?)	   "); 	
		
		try {
			con = dbMgr.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, user_id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				isOk = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con); // ����� �ڿ� �ݵ�� �ݳ��� �� - �ڹ� Ʃ���� ��������
		}
		return isOk;
	} ///////////////////////// end of idCheck

	// ȭ�� ó����
	public void initDisplay() {
		//ó�� ȭ���� ������ ���� ���̵� �ߺ��˻簡 ���� �ʾ����ϱ� ȸ������ ��ư ��Ȱ��ȭ
		jbtn_signup.setEnabled(false);
		// �̺�Ʈ �ҽ��� �̺�Ʈó�� �ڵ鷯 Ŭ���� �����ϱ�
		jbtn_zipcode.addActionListener(this);
		jbtn_idcheck.addActionListener(this);
		jbtn_signup.addActionListener(this);
		jp_center.setLayout(null);
		jlb_id.setBounds(20, 20, 100, 20);
		jtf_id.setBounds(120, 20, 100, 20);
		jbtn_idcheck.setBounds(230, 20, 120, 20);
		jlb_pw.setBounds(20, 45, 100, 20);
		jtf_pw.setBounds(120, 45, 100, 20);
		jlb_name.setBounds(20, 70, 100, 20);
		jtf_name.setBounds(120, 70, 100, 20);
		jlb_zipcode.setBounds(20, 95, 100, 20);
		jtf_zipcode.setBounds(120, 95, 70, 20);
		jbtn_zipcode.setBounds(200, 95, 120, 20);
		jlb_address.setBounds(20, 120, 100, 20);
		jtf_address.setBounds(120, 120, 200, 20);
		jp_center.add(jlb_id);
		jp_center.add(jtf_id);
		jp_center.add(jbtn_idcheck);
		jp_center.add(jlb_pw);
		jp_center.add(jtf_pw);
		jp_center.add(jlb_name);
		jp_center.add(jtf_name);
		jp_center.add(jlb_zipcode);
		jp_center.add(jtf_zipcode);
		jp_center.add(jbtn_zipcode);
		jp_center.add(jlb_address);
		jp_center.add(jtf_address);
		jp_south.add(jbtn_signup);
		jp_south.add(jbtn_cancel);
		this.add("Center", jsp);
		this.add("South", jp_south);
		this.setTitle("ȸ������ �ϱ�");
		this.setSize(430, 380);
		this.setVisible(true);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj == jbtn_zipcode) { // ��ư�� ������
			ZipcodeSearch zs = new ZipcodeSearch(this);
			zs.initDisplay();
		}
		
		else if(obj == jbtn_signup) {
			MemberVO pmVO = new MemberVO();
			pmVO.setMem_id(getId());
			pmVO.setMem_pw(getPw());
			pmVO.setMem_name(getName());
			pmVO.setMem_zipcode(getZipcode());
			pmVO.setMem_address(getAddress());
			
			int result = memberInsert(pmVO);
			
			if(result == 1) {
				System.out.println("result ===> " + result);
				// ȸ������ ���� �� MemberAppŬ������ ���ΰ�ħ �޼ҵ� ȣ���ϱ�
				// ���� ������ ����ȭ�� �ݾ��ֱ�
				this.dispose();
				memberApp.refreshData();
			}
		}
		// �� ���̵� �ߺ�üũ �Ϸ���?
		else if(obj == jbtn_idcheck) {
			boolean isOk = idCheck(getId());
			System.out.println("ID�ߺ�üũ ȣ��");
			
			if(isOk) {
				JOptionPane.showMessageDialog(this, "����� �� ���� ���̵� �Դϴ�.","ERROR",JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				JOptionPane.showMessageDialog
				(this, "����� �� �ִ� ���̵� �Դϴ�.","INFO",JOptionPane.INFORMATION_MESSAGE);
				isOk = true;
				jbtn_signup.setEnabled(isOk);
			}
		} 
	} /////////////////////// end of ActionEvent
	
	// �� �÷��� ������ �����ϰų� �о���� getter/setter�޼ҵ� 
	public String getId() { return jtf_id.getText(); }
	public void setId(String mem_id) { jtf_id.setText(mem_id); }
	public String getPw() { return jtf_pw.getText(); }
	public void setPw(String mem_pw) { jtf_pw.setText(mem_pw); }
	public String getName() { return jtf_name.getText(); }
	public void setName(String mem_name) { jtf_name.setText(mem_name); }
	public String getZipcode() { return jtf_zipcode.getText(); }
	public void setZipcode(String mem_zipcode) { jtf_zipcode.setText(mem_zipcode); }
	public String getAddress() { return jtf_address.getText(); }
	public void setAddress(String mem_address) { jtf_address.setText(mem_address); }

	public static void main(String[] args) {
		new MemberShip();
	}

}