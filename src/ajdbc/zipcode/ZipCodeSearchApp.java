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
	// JPanel�� ����Ʈ ���̾ƿ��� FlowLayout�� - ������� �¿�� ��ġ�鼭 ��ġ
	JPanel jp_north = new JPanel();
	
	String zdo 	= null; // ������ �� ���� ��� 
	String sigu = null; // ���õ� �ñ� ���� ��� 
	String dong	= null; // ���õ� �� ���� ���
	
	String zdos[]  = null; // �� �޺��ڽ��� ������ �ʱ�ȭ ���
	String sigus[] = null; // �ñ� �޺��ڽ��� ������ �ʱ�ȭ ���
	String dongs[] = null; // �� �޺��ڽ��� ������ �ʱ�ȭ ���
	
	JComboBox jcb_zdo	= null; // �� �޺��ڽ�
	JComboBox jcb_sigu	= null; // �ñ� �޺��ڽ�
	JComboBox jcb_dong	= null; // �� �޺��ڽ�
	
	String cols[] = {"�ּ�", "�����ȣ"};
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
	
	// ������ 
	public ZipCodeSearchApp() {
		zdos = getZDOList();
		
		//zdos  = new String[] {"��ü"};
		sigus = new String[] {"��ü"};
		dongs = new String[] {"��ü"};
		
		jcb_zdo  = new JComboBox(zdos);
		jcb_sigu = new JComboBox(sigus);
		jcb_dong = new JComboBox(dongs);
	}
	
	// �޺� �ڽ��� �ѷ��� zdo �÷��� ������ ����Ŭ �������� ��������
	public String[] getZDOList() {
		StringBuilder sql = new StringBuilder();
		
		sql.append("select '��ü' zdo from dual		 ");
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
	
	// �޺� �ڽ��� �ѷ��� sigu �÷��� ������ ����Ŭ �������� ��������
	public String[] getSIGUList(String zdo) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("select '��ü' sigu from dual         	 ");
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
	
	// �޺� �ڽ��� �ѷ��� sigu �÷��� ������ ����Ŭ �������� ��������
	public String[] getDONGList(String sigu) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("select '��ü' dong from dual        ");
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
				System.out.println("������ TOP ===> " + zdos[jcb_zdo.getSelectedIndex()]);
				zdo = zdos[jcb_zdo.getSelectedIndex()];
				jcb_sigu.removeAllItems();
				if("���".equals(zdo)) {
					for(int i=0;i<zdo.length();i++) {
						jcb_sigu.addItem(sigus[i]); // ����� sigus[i]
					}
				}
				else if("����".equals(zdo)) {
					for(int i=0;i<sigu.length();i++) {
						jcb_sigu.addItem(sigus[i]); // ������ sigus[]
					}
				}
				else if("����".equals(zdo)) {
					for(int i=0;i<dong.length();i++) {
						jcb_sigu.addItem(sigus[i]); // ������ sigus[]
					}
				}
			}
		}//////////////////////end of top
	}
	
	// ȭ��׸���
	public void initDisplay() {
		this.setTitle("�����ȣ �˻��� Ver1.0");
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
