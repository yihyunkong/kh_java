package ajdbc.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oracle.vo.DeptVO;

public class DeptController {
	DeptVO gdVO = null;
	
	private final String _DEL = "delete";
	private final String _INS = "insert";
	private final String _UPD = "update";
	private final String _SEL = "select";
	
	DeptDao  deptDao  = null;
	DeptView deptView = null;
	
	// ������
	public DeptController() {
		
	}
	
	public DeptController(DeptView deptView) {
		this.deptView = deptView;
		deptDao = new DeptDao(deptView);
	}
	
	public DeptController(DeptVO pdVO) {
		this.gdVO = pdVO;
	}
	
	public DeptVO send(DeptVO pdVO) {
		DeptVO rdVO = new DeptVO();
		
		// delete || update || insert
		String command = pdVO.getCommand();
		
		int result = 0;
		
		// �� �����Ұž�?
		if(_DEL.equals(command)) {
			result = deptDao.deptDelete(pdVO.getDeptno());
			
			if(result == 1) {
				rdVO.setResult(result);
			}
		}
		
		// �μ� ���� ����ҰŴ�?
		else if(_INS.equals(command)) {
			result = deptDao.deptInsert(pdVO);
			
			if(result == 1) {
				rdVO.setResult(result);
			}
		}
		
		// �μ� ���� ���� �����ž�?
		else if(_UPD.equals(command)) {
			result = deptDao.deptUpdate(pdVO);
			
			if(result == 1) {
				rdVO.setResult(result);
			}
		}
		
		// �μ� ���� �󼼺��� ����?
		else if(_SEL.equals(command)) {
			rdVO = deptDao.deptSelectDetail(pdVO.getDeptno());
		}
		return pdVO;
	}
	
	public List<Map<String, Object>> deptSelectAll() {
		List<Map<String, Object>> deptList = new ArrayList<>();
		deptList = deptDao.deptSelectAll();
		return deptList;
	}
	
}
