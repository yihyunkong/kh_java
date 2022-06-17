package ajdbc.dept;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginDao extends JFrame implements ActionListener {
   JPanel       jp_north      =  new JPanel();
   JTextField   jtf_id        =  new JTextField ("", 10);
   JButton      jbtn_check    =  new JButton("ID�ߺ��˻�");
   JButton      jbtn_join     =  new JButton("ȸ������");
   boolean		isIDCheck	  =  false;
   
   public LoginDao() {
      jbtn_check.addActionListener(this);
      initDisplay();
   }
   public void initDisplay() {
      jp_north.setLayout(new BorderLayout());
      jp_north.add("Center", jtf_id);
      jp_north.add("East",jbtn_check);
      this.add("North", jp_north);
      jbtn_join.setEnabled(false);
      this.add("South", jbtn_join);
      this.setSize(500,300);
      this.setVisible(true);
      
   }
   
   public String login(String mem_id, String mem_pw) {
	   String mem_name = null; // mem_name �ʱ�ȭ
	// ���������� ������ �ִ� ����Ŭ ������ ���� ��� �����
	      Connection       con = null;
	      // ����Ŭ ������ �ۼ��� select�� �����ϰ� ����Ŭ ������ ó�� ��û�� �� ���
	      PreparedStatement pstmt = null;
	      // ��ȸ ����� �ڹ��ڵ�� ������ �� �ʿ� - ����Ŭ ������ Ŀ���� �����ϴµ� �ʿ���.
	      ResultSet         rs   = null;
	      int result = -1;
	      StringBuilder sql = new StringBuilder(); 
		      sql.append("select result                                                ");
		      sql.append("from (                                                       ");
		      sql.append("           select                                            ");
		      sql.append("                    case when mem_id = ? then                ");
		      sql.append("                           case when mem_pw = ? then mem_name");
		      sql.append("                           else '0'                          ");
		      sql.append("                           end                               ");
		      sql.append("                    else '-1'                                ");
		      sql.append("                    end login                                ");
		      sql.append("            from member                                      ");
		      sql.append("           order by login desc                               ");
		      sql.append(")                                                            ");
		      sql.append("where rownum = 1											   ");
	          DBConnectionMgr dbMgr = new DBConnectionMgr();
	      try {
	         con = dbMgr.getConnection();
	         // ? �ڸ��� �� ���̵� �����ؾ� ��
	         pstmt = con.prepareStatement(sql.toString());
	         pstmt.setString(1, mem_id);
	         // selectó���ô� executeQuery()ȣ��
	         // insert, update, delete ó���ô� executeUpdate()ȣ��
	         rs = pstmt.executeQuery();
	         if(rs.next()) {
	            result = rs.getInt(1);
	         }
	         System.out.println("result : "+result);
	               
	      } catch (Exception e) {
	         System.out.println(e.toString());
	      }
	   return mem_name;
   }
   /******************************************************************************************************
    * ���̵� �ߺ�üũ
    * @param mem_id - ����ڰ� �Է��� ���̵�
    * @return 1: ���̵� ������, 0: ���̵� ��밡����, -1: ����Ʈ - end of file
    ******************************************************************************************************/
   
   
   public int idcheck(String mem_id) {
      // ���������� ������ �ִ� ����Ŭ ������ ���� ��� �����
      Connection       con = null;
      // ����Ŭ ������ �ۼ��� select�� �����ϰ� ����Ŭ ������ ó�� ��û�� �� ���
      PreparedStatement pstmt = null;
      // ��ȸ ����� �ڹ��ڵ�� ������ �� �ʿ� - ����Ŭ ������ Ŀ���� �����ϴµ� �ʿ���.
      ResultSet         rs   = null;
      int result = -1;
      StringBuilder sql = new StringBuilder(); 
          sql.append("SELECT NVL((                        	   ");
          sql.append("      SELECT 1                    	   ");
          sql.append("         FROM dual                	   ");
          sql.append("      WHERE EXISTS ( SELECT MEM_name	   ");
          sql.append("                     FROM member		   ");
          sql.append("                     WHERE mem_id=?)	   ");
          sql.append("   ),0)                                  ");
          sql.append("FROM dual                                ");
          DBConnectionMgr dbMgr = new DBConnectionMgr();
      try {
         con = dbMgr.getConnection();
         // ? �ڸ��� �� ���̵� �����ؾ� ��
         pstmt = con.prepareStatement(sql.toString());
         pstmt.setString(1, mem_id);
         // selectó���ô� executeQuery()ȣ��
         // insert, update, delete ó���ô� executeUpdate()ȣ��
         rs = pstmt.executeQuery();
         if(rs.next()) {
            result = rs.getInt(1);
         }
         System.out.println("result : "+result);
               
      } catch (Exception e) {
         System.out.println(e.toString());
      }
      return result;
   }
   public static void main(String[] args) {
      LoginDao loginDao = new LoginDao();
      loginDao.initDisplay();
   }
   @Override
   public void actionPerformed(ActionEvent e) {
      Object obj = e.getSource();
      // ID�ߺ�üũ��ư �����ž�?
      if(obj == jbtn_check) {
         System.out.println("ID�ߺ�üũ ȣ�� ����");
         // ����ڰ� �Է��� ���̵� ������ ���
         String user_id = jtf_id.getText();
         int result = idcheck(user_id);
         // �Է��� ���̵� �����ϴ°ž�?
         if(result == 1) {
            // �Է��� ���̵�� ��� ����
            // �ٽ� �Է��ؾ� �ȴ�.
            jtf_id.setText("");
            JOptionPane.showMessageDialog(this
            		, "�Է��� ���̵�� ����� �� �����ϴ�."
            		, "Error"
            		, JOptionPane.ERROR_MESSAGE);
            return;
         }
         // �Է��� ���̵� ���µ�?
         else if(result == 0) {
             // �Է��� ���̵� ����� �� �־�
        	 JOptionPane.showMessageDialog(this
             		, "�Է��� ���̵�� ����� �� �ֽ��ϴ�."
             		, "INFO"
             		, JOptionPane.INFORMATION_MESSAGE);
        	 jtf_id.setEditable(false);
        	 jbtn_check.setEnabled(false);
        	 isIDCheck = true;
        	 jbtn_join.setEnabled(isIDCheck);
        	 
         }
      }
   }

}
