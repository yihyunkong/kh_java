package ajdbc.member;

public class MemberSimulation {

	public static void main(String[] args) {
		MemberShip ms = new MemberShip();
//		boolean isOk = ms.idCheck("tomato");
//		System.out.println(isOk);
		MemberVO pmVO = new MemberVO();
		pmVO.setMem_id("test");
		pmVO.setMem_pw("123");
		pmVO.setMem_name("강감찬");
		pmVO.setMem_id("123456");
		pmVO.setMem_id("서울시 영등포구 당산동");
		
		int result = ms.memberInsert(null);
		System.out.println(result);
	}

}
