package ajdbc.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import ajdbc.dept.DBConnectionMgr;
import oracle.vo.DeptVO;


public class DeptDao {
	DeptView deptView = null;

	/////////////////////////////// DB연동 ///////////////////////////////
	DBConnectionMgr    	dbMgr          = new DBConnectionMgr();
	
	Connection          con            = null; // 연결통로
	PreparedStatement   pstmt          = null; // DML 구문 전달하고 오라클에게 요청
	ResultSet          	rs             = null; // 오라클 커서 조작
	/////////////////////////////// DB연동 ///////////////////////////////
	
	// 디폴트 생성자는 생성자가 하나도 없을 경우에만 제공됨
	// 파라미터를 갖는 생성자가 하나라도 있으면 디폴트 생성자도 제공안됨
	public DeptDao() {}
	public DeptDao(DeptView deptView) {
		this.deptView = deptView;
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
				deptView.setDeptno(0);
				deptView.setDname("");
				deptView.setLoc("");
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
				JOptionPane.showMessageDialog(deptView, "데이터가 수정되었습니다.", "Info", JOptionPane.INFORMATION_MESSAGE);
				deptSelectAll(); // 새로고침 처리 메소드 호출하기 - 메소드 재사용성 - 반복되는 코드를 줄여 준다.
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
			pstmt.setInt(1,  deptno);
			
			result = pstmt.executeUpdate();
			
			if(result == 1) {
				JOptionPane.showMessageDialog(deptView, "데이터가 삭제되었습니다.", "Info", JOptionPane.INFORMATION_MESSAGE);
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
			while(deptView.dtm.getRowCount() > 0) {
				// 파라미터에 0을 주어서 테이블의 인덱스가 바뀌는 문제를 해결한다.
				deptView.dtm.removeRow(0);
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
				deptView.dtm.addRow(oneRow);
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
				deptView.setDeptno(rdVO.getDeptno());
				deptView.setDname(rdVO.getDname());
				deptView.setLoc(rdVO.getLoc());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
		
		return rdVO;
	}
}
