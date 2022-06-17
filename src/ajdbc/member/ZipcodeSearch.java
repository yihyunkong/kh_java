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
	// 선언부
	/////////////////////////////// DB연동 ///////////////////////////////
	DBConnectionMgr    	dbMgr          = new DBConnectionMgr();
	
	Connection          con            = null; // 연결통로
	PreparedStatement   pstmt          = null; // DML 구문 전달하고 오라클에게 요청
	ResultSet          	rs             = null; // 오라클 커서 조작
	/////////////////////////////// DB연동 ///////////////////////////////

	JPanel jp_north = new JPanel();
	JTextField jtf_dong = new JTextField("동 이름을 입력하세요.", 20);
	JButton jbtn_search = new JButton("찾기");
	
	String zdos[] = {"전체", "서울", "경기"};
	JComboBox jcb = new JComboBox(zdos);
	
	String cols[] 	= {"우편번호", "주소"}; // JTable Header에 들어갈 1차 배열
	String data[][] = new String[0][2];	// Body 부분 data가 들어갈 2차 배열 
	// 0이여야 리프레쉬 - 지워진다.
	DefaultTableModel dtm  = new DefaultTableModel(data, cols);
	
	JTable			  jtb  = new JTable(dtm);
	
	Font			  font = new Font("돋움체", Font.BOLD, 18);
	JScrollPane		  jsp  = new JScrollPane(jtb);
	
	MemberShip		  ms   = null;
	
	// 셍성자
	public ZipcodeSearch() {
		
	}
	
	// memeberShip 클래스를 사용하기 위해서 파라미터 값을 통해 !
	public ZipcodeSearch(MemberShip ms) {
		this.ms = ms;
	}
	
	// 화면처리부
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
		this.setTitle("우편번호검색기"); // 창의 이름
		this.setSize(430, 400); // 창 사이즈
		this.setVisible(true); // 창이 보이도록 true
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
			con 	= dbMgr.getConnection(); // 오라클 서버에 연결하기
			pstmt 	= con.prepareStatement(sql.toString()); // 오라클 서버에 sql문 전달하기
			pstmt.setString(1, dong); // 사용자가 입력한 동 정보를 파라미터로 받아서 ?에 치환된다.
			rs		= pstmt.executeQuery(); // select문을 통해 커서로 table의 정보 확인하기
			
			Map<String, Object> rmap = null;
			
			while(rs.next()) {
				rmap = new HashMap<>();
				rmap.put("zipcode", rs.getString("zipcode"));
				rmap.put("address", rs.getString("address"));
				zipList.add(rmap);
			}
			
			// 조회된 결과를 defaultTableModel에 mapping 하기
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
	
	// 메인 메소드
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
			String user = jtf_dong.getText(); // 역삼, 당산, ...
			refreshData(user);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getClickCount() == 2) {
			System.out.println("더블클릭한거야?");
		}
		
		int index[] = jtb.getSelectedRows();
		
		if(index.length == 0) {
			JOptionPane.showMessageDialog(this, "조회할 데이터를 선택하세요.");
			return;
		} else {
			// 사용자가 더블클릭한 로유의 우편번호 가져오기
			String zipcode = (String)dtm.getValueAt(index[0], 0);
			// 사용자가 더블클릭한 로우의 주소 가져오기
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
