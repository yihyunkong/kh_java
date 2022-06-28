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
	// �����
	DBConnectionMgr dbMgr = new DBConnectionMgr();
	Connection 				con 	= null;
	PreparedStatement 		pstmt 	= null;
	ResultSet 				rs		= null;
	
	// ������
	
	// ����ڷκ��� ���� �Է� �޴� �޼ҵ� ����
	public String userInput() {
		String dong = null; // ���� �� >> ��
		// insert here
		Scanner scan= new Scanner(System.in); // ��ĳ�� ����Ͽ�
		dong = scan.nextLine(); // ����ڰ� �Է��ϴ� ���� dong�� �����ϱ�
		
		return dong; // return ���� ����ڰ� �Է��� dong�� �ȴ�.
	}
	// �����ȣ ��ȸ �޼ҵ� ����
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
	
	// ��ȸ�� �����ȣ ��� ����ϱ�
	
	// ���� �޼ҵ�
	public static void main(String[] args) {
		String userDong = null; // ����ڰ� �Է��� userDong �ʱ�ȭ
		ZipcodeSearch zs = new ZipcodeSearch(); // �ν��Ͻ�ȭ �Ͽ� ȣ���ϱ�
		
		while("1".equals(userDong) || userDong == null) {
			System.out.println("���� �Է��ϼ���. (��: ��굿)");
			userDong = zs.userInput(); // ����ڰ� �Է��� ���� userDong�� ����
			if("�׸�".equals(userDong)){
				break;
			}
			System.out.println("����ڰ� �Է��� �� ===> " + userDong);
			zs.getZipcodeList(userDong);
			userDong = "1";
		}
		System.out.println("while�� Ż�� ����");		
	}
}
