package ajdbc.zipcode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ZipcodeSearch {
	// 선언부
	DBConnectionMgr dbMgr = new DBConnectionMgr();
	Connection 				con 	= null;
	PreparedStatement 		pstmt 	= null;
	ResultSet 				rs		= null;
	
	// 생성자
	
	// 사용자로부터 동을 입력 받는 메소드 구현
	public String userInput() {
		String dong = null; // 리턴 값 >> 동
		// insert here
		Scanner scan= new Scanner(System.in); // 스캐너 사용하여
		dong = scan.nextLine(); // 사용자가 입력하는 값을 dong에 대입하기
		
		return dong; // return 값이 사용자가 입력한 dong이 된다.
	}
	// 우편번호 조회 메소드 구현
	private List<Map<String, Object>> getZipcodeList(String userDong) {
		List<Map<String, Object>> zipList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("select                        	");
        sql.append("	   address, zipcode         ");
        sql.append("  from zipcode_t           	    ");
        sql.append(" where dong like '%'||?||'%'    ");
        
        try {
        	con = dbMgr.getConnection();
        	pstmt = con.prepareStatement(sql.toString());
        	pstmt.setString(1, userDong);
        	rs = pstmt.executeQuery();
 
        	zipList = new ArrayList<>();
        	Map<String, Object> rmap = null;
        	
        	while(rs.next()) {
        		rmap = new HashMap<>();
        		rmap.put("address", rs.getString("address"));
        		rmap.put("zipcode", rs.getString("zipcode"));
        		//rmap.put("zipcode", rs.getString(2));
        		zipList.add(rmap);
        	}
        	System.out.println(zipList);
		} catch (Exception e) {
			System.out.println("Exception : " + e.toString());
		}
		return zipList;
	}
	
	// 조회된 우편번호 목록 출력하기
	
	// 메인 메소드
	public static void main(String[] args) {
		String userDong = null; // 사용자가 입력할 userDong 초기화
		ZipcodeSearch zs = new ZipcodeSearch(); // 인스턴스화 하여 호출하기
		
		while("1".equals(userDong) || userDong == null) {
			System.out.println("동을 입력하세요. (예: 당산동)");
			userDong = zs.userInput(); // 사용자가 입력한 값을 userDong에 대입
			if("그만".equals(userDong)){
				break;
			}
			System.out.println("사용자가 입력한 동 ===> " + userDong);
			zs.getZipcodeList(userDong);
			userDong = "1";
		}
		System.out.println("while문 탈출 성공");		
	}
}
