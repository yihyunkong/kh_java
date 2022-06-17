package ajdbc.zipcode;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ZipCodeSearchApp extends JFrame implements ItemListener {
	// JPanel은 디폴트 레이아웃이 FlowLayout임 - 가운데에서 좌우로 펼치면서 배치
	JPanel jp_north = new JPanel();
	
	String zdo 	= null; // 선택한 도 정보 담기 
	String sigu = null; // 선택된 시구 정보 담기 
	String dong	= null; // 선택된 동 정보 담기
	
	String zdos[]  = null; // 도 콤보박스에 데이터 초기화 사용
	String sigus[] = null; // 시구 콤보박스에 데이터 초기화 사용
	String dongs[] = null; // 동 콤보박스에 데이터 초기화 사용
	
	JComboBox jcb_zdo	= null; // 도 콤보박스
	JComboBox jcb_sigu	= null; // 시구 콤보박스
	JComboBox jcb_dong	= null; // 동 콤보박스
	
	String cols[] = {"주소", "우편번호"};
	String data[][] = new String[0][2];
	
	DefaultTableModel dtm = new DefaultTableModel(data, cols);
	JTable			  jtb = new JTable(dtm);
	JScrollPane		  jsp = 
			new JScrollPane(jtb
					, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
					, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	DBConnectionMgr	dbMgr = new DBConnectionMgr();
	
	Connection 			con = null;
	PreparedStatement pstmt = null;
	ResultSet 			 rs = null;
	
	// 생성자 
	public ZipCodeSearchApp() {
		zdos = getZDOList();
		
		//zdos  = new String[] {"전체"};
		sigus = new String[] {"전체"};
		dongs = new String[] {"전체"};
		
		jcb_zdo  = new JComboBox(zdos);
		jcb_sigu = new JComboBox(sigus);
		jcb_dong = new JComboBox(dongs);
	}
	
	// 콤보 박스에 뿌려질 zdo 컬럼의 정보를 오라클 서버에서 꺼내오기
	public String[] getZDOList() {
		StringBuilder sql = new StringBuilder();
		
		sql.append("select '전체' zdo from dual		 ");
		sql.append("union all                        ");
		sql.append("select zdo                       ");
		sql.append("  from (                         ");
		sql.append("        select distinct(zdo) zdo ");
		sql.append("          from zipcode_t         ");
		sql.append("        order by zdo asc         ");
		sql.append("       )                         ");
		
		try {
			con 	= dbMgr.getConnection();
			pstmt	= con.prepareStatement(sql.toString());
			rs 		= pstmt .executeQuery();
			
			Vector<String> v = new Vector<>();
			
			while(rs.next()) {
				String zdo = rs.getString("zdo");
				v.add(zdo);
			}
			zdos = new String[v.size()];
			v.copyInto(zdos);
		} catch (Exception e) {
			System.out.println("Exception : " + e.toString());
		}
		return zdos;
	}
	
	// 콤보 박스에 뿌려질 sigu 컬럼의 정보를 오라클 서버에서 꺼내오기
	public String[] getSIGUList(String zdo) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("select '전체' sigu from dual         	 ");
		sql.append("union all                            ");
		sql.append("select sigu                          ");
		sql.append(" from (                              ");
		sql.append("       select distinct(sigu) sigu 	 ");
		sql.append("         from zipcode_t            	 ");
		sql.append("        where zdo = ?             	 ");
		sql.append("       order by sigu asc         	 ");
		sql.append("      )                        	     ");
		
		try {
			con 	= dbMgr.getConnection();
			pstmt 	= con.prepareStatement(sql.toString());
			pstmt.setString(1, zdo);
			rs 		= pstmt .executeQuery();
			
			Vector<String> v = new Vector<>();
			
			while(rs.next()) {
				String sigu = rs.getString("sigu");
				v.add(sigu);
				
			}
			sigus = new String[v.size()];
			v.copyInto(sigus);
		} catch (Exception e) {
			System.out.println("Exception : " + e.toString());
		}
		return sigus;
	}
	
	// 콤보 박스에 뿌려질 sigu 컬럼의 정보를 오라클 서버에서 꺼내오기
	public String[] getDONGList(String sigu) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("select '전체' dong from dual        ");
		sql.append("union all                          ");
		sql.append("select dong                        ");
		sql.append("  from (                           ");
		sql.append("        select distinct(dong) dong ");
		sql.append("          from zipcode_t           ");
		sql.append("         where sigu = ?            ");
		sql.append("        order by dong asc          ");
		sql.append("       )                           ");
		
		try {
			con 	= dbMgr.getConnection();
			pstmt 	= con.prepareStatement(sql.toString());
			pstmt.setString(1, sigu);
			rs 		= pstmt .executeQuery();
			
			Vector<String> v = new Vector<>();
				
			while(rs.next()) {
				String dong = rs.getString("dong");
				v.add(dong);
				
			}
			dongs = new String[v.size()];
			v.copyInto(dongs);
		} catch (Exception e) {
			System.out.println("Exception : " + e.toString());
		}
		return dongs;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();
		if(obj == jcb_zdo) {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("선택한 TOP ===> " + zdos[jcb_zdo.getSelectedIndex()]);
				zdo = zdos[jcb_zdo.getSelectedIndex()];
				jcb_sigu.removeAllItems();
				if("경기".equals(zdo)) {
					for(int i=0;i<zdo.length();i++) {
						jcb_sigu.addItem(sigus[i]); // 경기의 sigus[i]
					}
				}
				else if("서울".equals(zdo)) {
					for(int i=0;i<sigu.length();i++) {
						jcb_sigu.addItem(sigus[i]); // 서울의 sigus[]
					}
				}
				else if("강원".equals(zdo)) {
					for(int i=0;i<dong.length();i++) {
						jcb_sigu.addItem(sigus[i]); // 강원의 sigus[]
					}
				}
			}
		}//////////////////////end of top
	}
	
	// 화면그리기
	public void initDisplay() {
		this.setTitle("우편번호 검색기 Ver1.0");
		jp_north.setBackground(Color.orange);
		jp_north.add(jcb_zdo);
		jp_north.add(jcb_sigu);
		jp_north.add(jcb_dong);
		this.add("North", jp_north);
		this.add("Center", jsp);
		this.setSize(600, 500);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		ZipCodeSearchApp zipApp = new ZipCodeSearchApp();
		zipApp.initDisplay();
	}

}
