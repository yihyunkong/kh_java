package ajdbc.member2;


import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;



public class MemberLogin extends JFrame implements ActionListener {
	/////////////////////////////////////////////////////
	/* 선언부 */
	/////////////////////////////////////////////////////
	
	//////////////////DB연동 ///////////////////
	DBConnectionMgr 	dbMgr 	= new DBConnectionMgr();
	Connection 			con 	= null;// 연결통로
	PreparedStatement 	pstmt 	= null;// DML구문 전달하고 오라클에게 요청
	ResultSet 			rs		= null;// 조회경우 커서를 조작 필요
	////////////////// DB연동 ///////////////////	

	String nickName = "";
	String imgPath = "src\\ajdbc\\dept\\";
	JLabel jlb_id = new JLabel("아이디");
	JLabel jlb_pw = new JLabel("패스워드");

	Font jl_font = new Font("휴먼매직체", Font.BOLD, 17);
	JTextField jtf_id = new JTextField("test");
	JPasswordField jpf_pw = new JPasswordField("123");

	// 로그인 버튼 
	JButton jbtn_login = new JButton(
			new ImageIcon(imgPath+"login.png"));
			//new ImageIcon("C:\\Users\\minkh\\Desktop\\practice\\dev_java\\src\\com\\Final\\image\\login.png"));
	// 회원가입 버튼 
	JButton jbtn_join = new JButton(
			new ImageIcon(imgPath+"confirm.png"));
			//new ImageIcon("C:\\Users\\minkh\\Desktop\\practice\\dev_java\\src\\com\\Final\\image\\co   nfirm.png"));

	// JPanel에 쓰일 이미지아이콘
	//ImageIcon ig = new ImageIcon("C:\\Users\\minkh\\Desktop\\practice\\dev_java\\src\\com\\Final\\image\\main.png");
	ImageIcon ig = new ImageIcon(imgPath+"main.PNG");

	/////////////////////////////////////////////////////
	/* 생성자 */
	/////////////////////////////////////////////////////
	public MemberLogin(){
		initDisplay();
	}

	/////////////////////////////////////////////////////
	/* jpanal 오버라이드 */
	/////////////////////////////////////////////////////

	/* 배경이미지 */
	class mypanal extends JPanel {
		public void paintComponent(Graphics g) {
			g.drawImage(ig.getImage(), 0, 0, null);
			setOpaque(false);
			super.paintComponents(g);
		}
	}

	/////////////////////////////////////////////////////
	/* 화면처리 */
	/////////////////////////////////////////////////////
	public void initDisplay() {
		setContentPane(new mypanal());
		
		/* 버튼과 텍스트필드 구성 */
		jbtn_join.addActionListener(this);
		jbtn_login.addActionListener(this);
		this.setLayout(null);
		this.setTitle("회원관리시스템 ver.1");
		this.setSize(350, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setLocation(800, 250);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// id 라인
		jlb_id.setBounds(45, 200, 80, 40);
		jlb_id.setFont(jl_font);
		jtf_id.setBounds(110, 200, 185, 40);
		this.add(jlb_id);
		this.add(jtf_id);

		// pw 라인
		jlb_pw.setBounds(45, 240, 80, 40);
		jlb_pw.setFont(jl_font);
		jpf_pw.setBounds(110, 240, 185, 40);
		this.add(jlb_pw);
		this.add(jpf_pw);

		// 로그인 버튼 라인
		jbtn_login.setBounds(175, 285, 120, 40);
		this.add(jbtn_login);

		// 회원가입 버튼 라인
		jbtn_join.setBounds(45, 285, 120, 40);
		this.add(jbtn_join);		
	}
	
	/****************************************************************************************
	 * 로그인 구현 
	 * @param mem_id - 사용자가 입력한 아이디 받아오기
	 * @param mem_pw - 사용자가 입력한 비번 받아오기 
	 * @return String mem_name - 사용자 이름 
	 ****************************************************************************************/
	// 로그인 메소드
	public String login(String mem_id, String mem_pw) {
		String mem_name = null;
		
		StringBuilder sql = new StringBuilder();                
		sql.append("select                                                 	");
        sql.append("	   mem_name                                        	");
        sql.append("  from (                                               	");
        sql.append("        select                                         	");
        sql.append("               case when mem_id = ? then             	");
        sql.append("                    case when mem_pw = ? then mem_name	");
        sql.append("                    else '0'                           	");
        sql.append("                    end                                	");
        sql.append("               else '-1'                              	");
        sql.append("               end as mem_name                        	");
        sql.append("          from member                                   ");
        sql.append("         order by mem_name desc                         ");
        sql.append("	   )                                          		");
        sql.append(" where rownum = 1                               		");
		
		try {
			con = dbMgr.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, mem_id);
			pstmt.setString(2, mem_pw);
			rs = pstmt.executeQuery();
			
//			System.out.println(rs.next()); // true - 커서가 다음으로 이동됨
			
			// 조회 결과는 0이거나, 1row이므로 if문 처리 가능
			if(rs.next()) { // false
				mem_name = rs.getString("mem_name");
			}
			System.out.println("mem_name : " + mem_name);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			
		}
		return mem_name;
	}

	// 메인 메소드
	public static void main(String[] args) throws Exception {
		new MemberLogin();
	}
	
	// 회원가입 또는 로그인 버튼을 눌렀을 때 actionEvent >> 창 띄우기 !
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == jbtn_join) {
			Member2Ship ship = new Member2Ship();
			ship.initDisplay();
		} 
		else if (obj == jbtn_login) {
			String user_id = jtf_id.getText();
			String user_pw = jpf_pw.getText();
			
			String mem_name = null;
			mem_name = login(user_id, user_pw);
			
			System.out.println("로그인 요청 결과는? " + mem_name);
			
			if(mem_name == null) {
				JOptionPane.showMessageDialog(this, "회원가입 여부를 확인하세요.");
				return;
			} else if(mem_name.length() > 2) {
				this.dispose();
				new Member2App();
			}
		}
	}
}
