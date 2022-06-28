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

// 화면 구현시 JFrame
// JFrame jf = new JFrame();
// jf.setTitle("자바실습");
// JFrame jf = new JFrame();

public class MemberApp extends JFrame implements ActionListener, MouseListener {
	/////////////////////////////// DB연동 ///////////////////////////////
	DBConnectionMgr    	dbMgr       = new DBConnectionMgr();
	
	Connection          con         = null; // 연결통로
	PreparedStatement   pstmt       = null; // DML 구문 전달하고 오라클에게 요청
	ResultSet          	rs          = null; // 오라클 커서 조작
	/////////////////////////////// DB연동 ///////////////////////////////
	
	// 속지
	JPanel            jp_north      = new JPanel();
	
	// 버튼
	JButton           jbtn_sel      = new JButton("조회");
	JButton           jbtn_ins      = new JButton("입력");
	JButton        	  jbtn_upd      = new JButton("수정");
	JButton           jbtn_del      = new JButton("삭제");
	
	String 			  cols[] 		= {"번호", "아이디", "이름", "주소"}; // JTable Header에 들어갈 1차 배열
	String 			  data[][] 		= new String[0][4];				// Body 부분 data가 들어갈 2차 배열 
	// 0이여야 리프레쉬 - 지워진다.
	DefaultTableModel dtm  			= new DefaultTableModel(data, cols);
	
	JTable			  jtb  = 		new JTable(dtm);
	
	Font			  font =	 	new Font("돋움체", Font.BOLD, 18);
	JScrollPane		  jsp  = 		new JScrollPane(jtb);
		
	// 생성자 안에서 initDisplay() : 메소드가 호출되는 위치를 변경해보자
	MemberShip 		  ms   =		new MemberShip(this); // this와 default의 차이를 알아보기 !
	
	// 생성자
	public MemberApp() {
		// 이벤트 소스와 이벤트 처리 클래스를 매핑
		jbtn_sel.addActionListener(this);
	    jbtn_ins.addActionListener(this);
	    jbtn_upd.addActionListener(this);
	    jbtn_del.addActionListener(this);
	    
		initDisplay();
		refreshData();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource(); // 이벤트가 발생하는 이벤트 소스의 주소번지를 가져온다.
	
		if(obj == jbtn_ins) {
			ms.initDisplay();
			System.out.println("전체 조회 호출 성공");
		} else if(obj == jbtn_sel) {
			refreshData();
		}
	}
	
	public void refreshData() {
		List<Map<String, Object>> memList = new ArrayList<>(); // 리스트는 순서가 있어서 느리다. 맵은 속도가 빠르지만 순서가 없다.
	
		// sql문 실행하기
		StringBuilder sql = new StringBuilder();
		sql.append("select mem_no, mem_id, mem_pw, mem_name, mem_zipcode, mem_address ");
		sql.append("  from member                                                   ");
		sql.append("order by mem_no desc                                            ");
		
		try {
			con 	= dbMgr.getConnection(); // 오라클 서버에 연결하기
			pstmt 	= con.prepareStatement(sql.toString()); // 오라클 서버에 sql문 전달하기
			rs		= pstmt.executeQuery(); // select문을 통해 커서로 table의 정보 확인하기
			
			Map<String, Object> rmap = null; // 정보를 담을 rmap을 Map(속도가 빠르지만 순서가 없는)으로 선언하기
			
			while(rs.next()) {
				rmap = new HashMap<>(); // HashMap을 통해 rmap의 값 담기
				//rmap에 넣을 정보들 !
				rmap.put("mem_no", rs.getInt("mem_no"));
				rmap.put("mem_id", rs.getString("mem_id"));
				rmap.put("mem_name", rs.getString("mem_name"));
				rmap.put("mem_address", rs.getString("mem_address"));
				
				memList.add(rmap); // memList에 rmap 추가하기
			}
//			System.out.println(memList);
			
			// 기존에 조회된 결과, 즉 다시 조회 버튼을 누르면 남아있는 목록 삭제하기 >> 새롭게 목록 조회
			// 데이터를 가지고 있는건 defaultTableModel (dtm)
			while(dtm.getRowCount() > 0) { // 0 보다 크면 테이블안에 정보가 남아있다.
				dtm.removeRow(0);
			}
			
			// iterator는 자료구조가 갖고있는 정보의 유무를 체크하는데 필요한 메소드를 제공하고 있다.
			Iterator<Map<String, Object>> iter = memList.iterator();			
			
			// 여기서부터 모르겠어ㅠ
			// keys[]라는 배열에 데이터를 담기 위해서?
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
			DBConnectionMgr.freeConnection(rs, pstmt, con); // 사용한 자원 반납하기 (생성된 역순으로)
		}
		
	}
	
	// 화면 선언부
	public void initDisplay() {
		jp_north.setLayout(new FlowLayout(FlowLayout.LEFT)); // 버튼을 왼쪽에 배칠할거야
		
		jbtn_sel.setBackground(new Color(158, 9, 9)); // 배경색
		jbtn_sel.setForeground(new Color(212, 212, 212)); // 글자색
		jp_north.add(jbtn_sel); // 조회버튼
		
		jbtn_ins.setBackground(new Color(7, 84, 170));
		jbtn_ins.setForeground(new Color(212, 212, 212));
		jp_north.add(jbtn_ins); // 입력버튼
		
		jbtn_upd.setBackground(new Color(19, 99, 57));
		jbtn_upd.setForeground(new Color(212, 212, 212));
		jp_north.add(jbtn_upd); // 수정버튼
		
		jbtn_del.setBackground(new Color(54, 54, 54));
		jbtn_del.setForeground(new Color(212, 212, 212));
		jp_north.add(jbtn_del); // 삭제버튼
		
		this.add("North", jp_north); // 북쪽에 jp_north라는 속지에 버튼을 add 할거야
		
		this.add("Center", jsp);
		this.setTitle("회원관리시스템 ver1.0"); // 창의 이름
		this.setSize(600, 400); // 창 사이즈
		this.setVisible(true); // 창이 보이도록 true
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


	// 메인메소드
	public static void main(String[] args) {
		new MemberApp();
	}

}
