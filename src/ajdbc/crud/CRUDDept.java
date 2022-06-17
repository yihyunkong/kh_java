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

// 단일 상속의 단점을 보완하기 위해서 인터페이스를 제공하고 있다. 
// 인터페이스는 다중 처리 가능하다.
public class CRUDDept extends JFrame implements ActionListener, MouseListener {
	// 선언부
	/////////////////////////////// DB연동 ///////////////////////////////
	DBConnectionMgr 	dbMgr 			= new DBConnectionMgr();
	
	Connection 			con 			= null; // 연결통로
	PreparedStatement 	pstmt 			= null; // DML 구문 전달하고 오라클에게 요청
	ResultSet 			rs 				= null; // 오라클 커서 조작
	/////////////////////////////// DB연동 ///////////////////////////////
	
	// JFrame의 디폴트 레이아웃은 BorderLayout
	JPanel  			jp_north 		= new JPanel();				// 디폴트레이아웃:FlowLayout
	JButton 			jbtn_sel 		= new JButton("조회");
	JButton 			jbtn_ins 		= new JButton("입력");
	JButton 			jbtn_upd 		= new JButton("수정");
	JButton 			jbtn_del 		= new JButton("삭제");
	
	// 서로 의존 관계에 있다. - 의존성 주입(인스턴스화 - 싱글톤 패턴), 객체 주입법, annotation 
	String				cols[]			= {"부서번호", "부서명", "지역"};
	String				data[][]		= new String[0][3];
	DefaultTableModel 	dtm				= new DefaultTableModel(data, cols);
	JTable				jtb				= new JTable(dtm);
	JScrollPane 		jsp				= new JScrollPane(jtb);
	
	// 테이블의 로우에 바인딩하기 - UI solution 기본 제공
	JPanel	   			jp_south		= new JPanel();				// 디폴트레이아웃:FlowLayout
	JTextField 			jtf_deptno 		= new JTextField("", 10);
	JTextField 			jtf_dname 		= new JTextField("", 20);
	JTextField 			jtf_loc 		= new JTextField("", 20);
	
	// 생성자 - 이벤트 소스, 이벤트 핸들러 mapping
	// >> 내가 이벤트 처리를 담당하는 핸들러 클래스이다. 
	// >> ActionListner al = ne CRUDDept();
	// >> 선언부와 생성부의 클래스 이름이 다르다.
	// >> 다형성을 누릴 수 있다.
	// >> 클래스 사이의 결합도를 낮출 수 있어서 단위 테스트가 가능한 구조가 된다. 
	// >> 생성부의 이름으로 생성된다는 것.
	public CRUDDept() {
		jbtn_sel.addActionListener(this);
		jbtn_ins.addActionListener(this);
		jbtn_upd.addActionListener(this);
		jbtn_del.addActionListener(this);
		
		jtb.addMouseListener(this);
		
		initDisplay();
	}
	
	/************************************************************************
	 * 부서 등록 구현하기
	 * VO(Value Object) - 오라클의 타입과 자바의 타입을 비교해서 동기화를 하는 구조 
	 * 					  (컬럼명과 VO의 전변과 Map의 key값은 반드시 일치해야한다.)
	 * @param pdVO - 사용자가 입력한 부서번호, 부서명, 지역을 받는다. - 복합데이터 클래스 
	 * @return int - 1: 등록 성공, 0: 등록 실패
	 * insert into dept(deptno, dname, loc)			
             values(71, '개발1팀', '서귀포')			
	 ************************************************************************/
	// 부서입력 메소드
	public int deptInsert(DeptVO pdVO) {
		System.out.println("deptInsert 호출 성공");
		int result = 0;
		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into dept(deptno, dname, loc) values(?, ?, ?)");
        
		// 물리적으로 떨어져있는 오라클 서버와 통신한다. 
		// 반드시 예외처리를 해야한다.
		// 사용한 자원을 반납처리 해야한다. - 명시적으로 함
		// 생성된 역순으로 반납한다. 왜 생성한 역순인가?
		// >> 의존관계에 있기 때문이다. Connection, PreparedStatement, ResultSet - 자바 성능 튜닝 가이드
        try {
        	con 	= dbMgr.getConnection();
			pstmt 	= con.prepareStatement(sql.toString());
			
			// 동적 쿼리를 처리하는 PreparedStatement에서 ? 자리에 필요한 파라미터를 적용하는데
			// 테이블 설계가 바뀌거나 컬럼이 추가되는 경우를 예측하여 최소한 코드 변경이 되도록 변수를 사용한다.
			// ? 자리는 1부터이므로 ++i로 시작한다. 
			// 만일 1로 초기화 했다.면 i++로 시작해야한다.
			int i = 0; // 변수를 선언해서 중간에 새로 생성된 컬럼이 끼게 되더라도 수정할 필요가 없다 ! 
			
			pstmt.setInt(++i, pdVO.getDeptno());
			pstmt.setString(++i, pdVO.getDname());
			pstmt.setString(++i, pdVO.getLoc());
			
			// select인 경우 커서를 리턴 받고, insert, update, delete인 경우 int를 리턴 받는다. 
			result = pstmt.executeUpdate();
			
			// 오라클 서버에서 입력 처리를 성공했을 때, 1을 돌려 받는다.
			if(result == 1) {
				deptSelectAll();
				// 입력 성공 후에 화면에 대한 초기화 - 사용자의 편의성 제공
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
	 * 부서 수정 구현하기
	 * @param pdVO - 사용자가 입력한 부서번호, 부서명, 지역을 받는다. - 복합데이터 클래스 
	 * @return int - 1: 등록 성공, 0: 등록 실패
	 * update dept		
     	  set dname = '개발2팀'		
             ,loc   = '거제도'		
        where deptno = 71
	 ************************************************************************/
	// 부서수정 메소드
	public int deptUpdate(DeptVO pdVO) {
		System.out.println("deptUpdate 호출 성공");
		
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
				JOptionPane.showMessageDialog(this, "데이터가 수정되었습니다.", "Info", JOptionPane.INFORMATION_MESSAGE);
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
	 * 부서 삭제 구현하기
	 * @param deptno(int) - 사용자가 선택한 부서번호, 부서명, 지역을 받는다. - 복합데이터 클래스 
	 * @return int - 1: 등록 성공, 0: 등록 실패
	 * delete from dept	
		where deptno = 71
	 ************************************************************************/
	// 부서삭제 메소드
	public int deptDelete (int deptno) { // 파라미터 값으로 pk값 받기
		System.out.println("deptUpdate 호출 성공 : " + deptno);
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
				JOptionPane.showMessageDialog(this, "데이터가 삭제되었습니다.", "Info", JOptionPane.INFORMATION_MESSAGE);
				// 삭제된 후에 화면 갱신 처리하기 - 동기화 처리 진행됨
				// 입력, 수정, 삭제에서 반복적으로 호출될 수 있다.
				// List<VO>, List<Map>
				deptSelectAll(); // 새로고침 처리 메소드 호출하기 - 메소드 재사용성 - 반복되는 코드를 줄여준다.
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(pstmt, con);
		}
		return result;
	}
	
	/************************************************************************
	 * 부서목록 전체 조회 구현 (새로고침 시 재사용 위해서)
	 * @return List<Map<String, Object>>
	 * select deotno, dname, loc from dept
	 ***********************************************************************/
	// 전체조회 메소드
	public List<Map<String, Object>> deptSelectAll() {
		System.out.println("deptSelectAll 호출 성공");
		List<Map<String, Object>> deptList = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select deptno, dname, loc from dept");
		
		try {
			con 	= dbMgr.getConnection();				// 연결
			pstmt 	= con.prepareStatement(sql.toString()); // 동적쿼리
			rs		= pstmt.executeQuery();					// 커서
			
			Map<String, Object> rmap = null;
			
			while(rs.next()) {
				rmap = new HashMap<>(); // 같은 이름의 변수이지만, 서로 다른 주소번지를 갖는다. 
				
				rmap.put("deptno", rs.getInt("deptno"));
				rmap.put("dname", rs.getString("dname"));
				rmap.put("loc", rs.getString("loc"));
				
				deptList.add(rmap); // 순서가 정해진다. 기본정렬은 오라클에서 하는 것이 빠르다. (order by, index)
			}
//			System.out.println(deptList);
			
			// 기존에 조회된 결과, 즉 목록을 삭제하기. 
			while(dtm.getRowCount() > 0) {
				// 파라미터에 0을 주어서 테이블의 인덱스가 바뀌는 문제를 해결한다.
				dtm.removeRow(0);
			}
			
			// Iterator는 자료구조가 갖고있는 정보릐 유무를 체크하는데 필요한 메소드를 제공하고 있다.
			Iterator<Map<String, Object>> iter = deptList.iterator();
			
			Object keys[] = null;
			
			while(iter.hasNext()) {
				Map<String, Object> data = iter.next();
				keys = data.keySet().toArray();
				Vector<Object> oneRow = new Vector<>();
				oneRow.add(data.get(keys[2]));
				oneRow.add(data.get(keys[1]));
				oneRow.add(data.get(keys[0]));
				// 데이터셋인 DefaultTableModel에 조회 결과 담기 - 반복처리함 => 10, 20, 30, 40
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
	 * 부서목록 상세 조회 구현
	 * @param  deptno(int)
	 * @return DeptVO
	 * selelct deptno, dname, loc from dept
		 where deptno = ?
	 ***********************************************************************/
	// 상세조회 메소드
	public DeptVO deptSelectDetail(int deptno) {
		System.out.println("deptSelectDetail 호출 성공");
		
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
	
	// 화면처리부
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
		this.setTitle("부서관리시스템");
		this.setSize(600, 400);
		this.setVisible(true);
	}
	
	// 메인메소드
	public static void main(String[] args) {
		new CRUDDept();
	}

	// 추상메소드
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		// 너 조회 누른거야?
		if(obj == jbtn_sel) {
			System.out.println("전체 조회 호출 성공");
			
			deptSelectAll();
		}
		
		// 입력하고 싶니?
		else if(obj == jbtn_ins) {
			System.out.println("입력 호출 성공");
			
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
		// 수정할거야?
		else if(obj == jbtn_upd) {
			System.out.println("수정 호출 성공");
			String deptno 	= getDeptno();
			String dname 	= getDname();
			String loc 		= getLoc();
			
			DeptVO pdVO = new DeptVO();
			pdVO.setDeptno(Integer.parseInt(deptno));
			pdVO.setDname(dname);
			pdVO.setLoc(loc);
			deptUpdate(pdVO);
		}
		
		// 삭제를 윈해? : view -> action(delete) -> action(select all) -> view
		else if(obj == jbtn_del) {
			System.out.println("삭제 호출 성공");
			
			int index[] = jtb.getSelectedRows();
			
			if(index.length == 0) {
				JOptionPane.showMessageDialog(this, "삭제할 데이터를 선택하세요.....", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				Integer deptno = (Integer)dtm.getValueAt(index[0], 0);
				System.out.println("사용자가 선택한 부서번호 : " + deptno);
				deptDelete(deptno);
			}
		}
	} /////////////////////////////////////////////////////////// end of actionPerformed
	
	// 각 컬럼의 값들을 설정하거나 읽어오는 getter-setter 메소드
	public String getDeptno() { return jtf_deptno.getText(); }
	public void setDeptno (int deptno)  { jtf_deptno.setText(String.valueOf(deptno)); }
	public String getDname () { return jtf_dname.getText(); }
	public void setDname  (String dname)   { jtf_dname.setText(dname); }
	public String getLoc   () { return jtf_loc.getText(); }
	public void setLoc    (String loc)     { jtf_loc.setText(loc); }

	@Override
	public void mouseClicked(MouseEvent e) {
		int index[] = jtb.getSelectedRows();
		
		// 테이블의 데이터를 선택하지 않은 경우
		if(index.length == 0) {
			JOptionPane.showMessageDialog(this, "조회할 데이터를 선택하세요.", "Error", JOptionPane.ERROR_MESSAGE);
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
