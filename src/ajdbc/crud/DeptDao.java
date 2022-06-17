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

	/////////////////////////////// DB���� ///////////////////////////////
	DBConnectionMgr    	dbMgr          = new DBConnectionMgr();
	
	Connection          con            = null; // �������
	PreparedStatement   pstmt          = null; // DML ���� �����ϰ� ����Ŭ���� ��û
	ResultSet          	rs             = null; // ����Ŭ Ŀ�� ����
	/////////////////////////////// DB���� ///////////////////////////////
	
	// ����Ʈ �����ڴ� �����ڰ� �ϳ��� ���� ��쿡�� ������
	// �Ķ���͸� ���� �����ڰ� �ϳ��� ������ ����Ʈ �����ڵ� �����ȵ�
	public DeptDao() {}
	public DeptDao(DeptView deptView) {
		this.deptView = deptView;
	}

	/************************************************************************
	 * �μ� ��� �����ϱ�
	 * VO(Value Object) - ����Ŭ�� Ÿ�԰� �ڹ��� Ÿ���� ���ؼ� ����ȭ�� �ϴ� ���� 
	 * 					  (�÷���� VO�� ������ Map�� key���� �ݵ�� ��ġ�ؾ��Ѵ�.)
	 * @param pdVO - ����ڰ� �Է��� �μ���ȣ, �μ���, ������ �޴´�. - ���յ����� Ŭ���� 
	 * @return int - 1: ��� ����, 0: ��� ����
	 * insert into dept(deptno, dname, loc)			
             values(71, '����1��', '������')			
	 ************************************************************************/
	// �μ��Է� �޼ҵ�
	public int deptInsert(DeptVO pdVO) {
		System.out.println("deptInsert ȣ�� ����");
		int result = 0;
		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into dept(deptno, dname, loc) values(?, ?, ?)");
        
		// ���������� �������ִ� ����Ŭ ������ ����Ѵ�. 
		// �ݵ�� ����ó���� �ؾ��Ѵ�.
		// ����� �ڿ��� �ݳ�ó�� �ؾ��Ѵ�. - ��������� ��
		// ������ �������� �ݳ��Ѵ�. �� ������ �����ΰ�?
		// >> �������迡 �ֱ� �����̴�. Connection, PreparedStatement, ResultSet - �ڹ� ���� Ʃ�� ���̵�
        try {
        	con 	= dbMgr.getConnection();
			pstmt 	= con.prepareStatement(sql.toString());
			
			// ���� ������ ó���ϴ� PreparedStatement���� ? �ڸ��� �ʿ��� �Ķ���͸� �����ϴµ�
			// ���̺� ���谡 �ٲ�ų� �÷��� �߰��Ǵ� ��츦 �����Ͽ� �ּ��� �ڵ� ������ �ǵ��� ������ ����Ѵ�.
			// ? �ڸ��� 1�����̹Ƿ� ++i�� �����Ѵ�. 
			// ���� 1�� �ʱ�ȭ �ߴ�.�� i++�� �����ؾ��Ѵ�.
			int i = 0; // ������ �����ؼ� �߰��� ���� ������ �÷��� ���� �Ǵ��� ������ �ʿ䰡 ���� ! 
			
			pstmt.setInt(++i, pdVO.getDeptno());
			pstmt.setString(++i, pdVO.getDname());
			pstmt.setString(++i, pdVO.getLoc());
			
			// select�� ��� Ŀ���� ���� �ް�, insert, update, delete�� ��� int�� ���� �޴´�. 
			result = pstmt.executeUpdate();
			
			// ����Ŭ �������� �Է� ó���� �������� ��, 1�� ���� �޴´�.
			if(result == 1) {
				deptSelectAll();
				// �Է� ���� �Ŀ� ȭ�鿡 ���� �ʱ�ȭ - ������� ���Ǽ� ����
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
	 * �μ� ���� �����ϱ�
	 * @param pdVO - ����ڰ� �Է��� �μ���ȣ, �μ���, ������ �޴´�. - ���յ����� Ŭ���� 
	 * @return int - 1: ��� ����, 0: ��� ����
	 * update dept		
     	  set dname = '����2��'		
             ,loc   = '������'		
        where deptno = 71
	 ************************************************************************/
	// �μ����� �޼ҵ�
	public int deptUpdate(DeptVO pdVO) {
		System.out.println("deptUpdate ȣ�� ����");
		
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
				JOptionPane.showMessageDialog(deptView, "�����Ͱ� �����Ǿ����ϴ�.", "Info", JOptionPane.INFORMATION_MESSAGE);
				deptSelectAll(); // ���ΰ�ħ ó�� �޼ҵ� ȣ���ϱ� - �޼ҵ� ���뼺 - �ݺ��Ǵ� �ڵ带 �ٿ� �ش�.
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
		
		return result;
	}
	
	/************************************************************************
	 * �μ� ���� �����ϱ�
	 * @param deptno(int) - ����ڰ� ������ �μ���ȣ, �μ���, ������ �޴´�. - ���յ����� Ŭ���� 
	 * @return int - 1: ��� ����, 0: ��� ����
	 * delete from dept	
		where deptno = 71
	 ************************************************************************/
	// �μ����� �޼ҵ�
	public int deptDelete (int deptno) { // �Ķ���� ������ pk�� �ޱ�
		System.out.println("deptUpdate ȣ�� ���� : " + deptno);
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
				JOptionPane.showMessageDialog(deptView, "�����Ͱ� �����Ǿ����ϴ�.", "Info", JOptionPane.INFORMATION_MESSAGE);
				// ������ �Ŀ� ȭ�� ���� ó���ϱ� - ����ȭ ó�� �����
				// �Է�, ����, �������� �ݺ������� ȣ��� �� �ִ�.
				// List<VO>, List<Map>
				deptSelectAll(); // ���ΰ�ħ ó�� �޼ҵ� ȣ���ϱ� - �޼ҵ� ���뼺 - �ݺ��Ǵ� �ڵ带 �ٿ��ش�.
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(pstmt, con);
		}
		return result;
	}
	
	/************************************************************************
	 * �μ���� ��ü ��ȸ ���� (���ΰ�ħ �� ���� ���ؼ�)
	 * @return List<Map<String, Object>>
	 * select deotno, dname, loc from dept
	 ***********************************************************************/
	// ��ü��ȸ �޼ҵ�
	public List<Map<String, Object>> deptSelectAll() {
		System.out.println("deptSelectAll ȣ�� ����");
		
		List<Map<String, Object>> deptList = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select deptno, dname, loc from dept");
		
		try {
			con 	= dbMgr.getConnection();				// ����
			pstmt 	= con.prepareStatement(sql.toString()); // ��������
			rs		= pstmt.executeQuery();					// Ŀ��
			
			Map<String, Object> rmap = null;
			
			while(rs.next()) {
				rmap = new HashMap<>(); // ���� �̸��� ����������, ���� �ٸ� �ּҹ����� ���´�. 
				
				rmap.put("deptno", rs.getInt("deptno"));
				rmap.put("dname", rs.getString("dname"));
				rmap.put("loc", rs.getString("loc"));
				
				deptList.add(rmap); // ������ ��������. �⺻������ ����Ŭ���� �ϴ� ���� ������. (order by, index)
			}
//			System.out.println(deptList);
			
			// ������ ��ȸ�� ���, �� ����� �����ϱ�. 
			while(deptView.dtm.getRowCount() > 0) {
				// �Ķ���Ϳ� 0�� �־ ���̺��� �ε����� �ٲ�� ������ �ذ��Ѵ�.
				deptView.dtm.removeRow(0);
			}
			
			// Iterator�� �ڷᱸ���� �����ִ� �����l ������ üũ�ϴµ� �ʿ��� �޼ҵ带 �����ϰ� �ִ�.
			Iterator<Map<String, Object>> iter = deptList.iterator();
			
			Object keys[] = null;
			
			while(iter.hasNext()) {
				Map<String, Object> data = iter.next();
				keys = data.keySet().toArray();
				Vector<Object> oneRow = new Vector<>();
				oneRow.add(data.get(keys[2]));
				oneRow.add(data.get(keys[1]));
				oneRow.add(data.get(keys[0]));
				// �����ͼ��� DefaultTableModel�� ��ȸ ��� ��� - �ݺ�ó���� => 10, 20, 30, 40
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
	 * �μ���� �� ��ȸ ����
	 * @param  deptno(int)
	 * @return DeptVO
	 * selelct deptno, dname, loc from dept
		 where deptno = ?
	 ***********************************************************************/
	// ����ȸ �޼ҵ�
	public DeptVO deptSelectDetail(int deptno) {
		System.out.println("deptSelectDetail ȣ�� ����");
		
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
