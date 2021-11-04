package user.qna.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.my.travelExpedition.utility.WebUtil;

import user.postimg.model.PostimgBean;
import user.postimg.model.PostimgDao;
import user.qna.model.QnaBean;
import user.qna.model.QnaDao;

@Controller
public class QnaDeleteController {
	
	public static final String COMMAND = "/qnaDelete.do";
	public static final String GETPAGE = "user/qna/qnaDeleteForm";
	public static final String GOTOPAGE = "redirect:/qnaList.do";
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private QnaDao qnaDao;
	
	@Autowired
	private PostimgDao postimgDao;
	
	@RequestMapping(value = COMMAND, method = RequestMethod.GET)
    public ModelAndView qnaUpdateView(HttpServletRequest request,
    								  @RequestParam Map<String, String> map) {
		
		ModelAndView mav = new ModelAndView(GETPAGE);
		
		Map<String, Object> qnaMap = new HashMap<String, Object>();
		qnaMap.put("num", map.get("num"));
		QnaBean qna = qnaDao.getQnaDetail(qnaMap);
		
		//여럿 파일 이미지들(이미지관리테이블) 조회
		Map<String, Object> imgMap = new HashMap<String, Object>();
		imgMap.put("acode", "3"); //문의 구분자값
		imgMap.put("anum", map.get("num")); 
		List<PostimgBean> imgList = postimgDao.getPostimgList(imgMap);
		//System.out.println("lists:" + lists.toString());
		
		mav.addObject("pageNumber", map.get("pageNumber"));
		mav.addObject("qna", qna); //문의 상세
		mav.addObject("imgList", imgList); //이미지리스트
		
		return mav;
	}
	
	@RequestMapping(value = COMMAND, method = RequestMethod.POST)
    public ModelAndView doActionPost(ModelAndView mav,@RequestParam Map<String, Object> map) {
		
		//System.out.println("map:" + map.get("num") );
		
		try {
			//여럿 파일 이미지들(이미지관리테이블) 조회
			map.put("acode", "3"); //문의 구분자값
			map.put("anum", map.get("num")); 
			List<PostimgBean> postImglists = postimgDao.getPostimgList(map);
			//System.out.println("DB 이미지 갯수 :" + postImglists.size());
			
			// (이미지관리테이블 삭제) 기존파일 삭제를 위해 이름을 조회한다
			String[] arPostImgFileName = new String[postImglists.size()];
			for(int i = 0; i < postImglists.size(); i++) {
				arPostImgFileName[i] = postImglists.get(i).getImgname(); //이미지명
			}
			
			// 이미지 테이블에 기존이미지들 파일들 삭제
			WebUtil.deletefileUpload(servletContext, arPostImgFileName);
			
			// 기존파일 DB에서 삭제
			postimgDao.deletePostimgData(map);
			
			int num = Integer.parseInt((String)map.get("num"));
			//System.out.println("num::::" + num);
			
			//문의 테이블 데이터 삭제
			qnaDao.deleteData(num);
			
			mav.setViewName(GOTOPAGE);
			
			mav.addObject("pageNumber", map.get("pageNumber"));
			
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("내부오류");
		}
		
		return mav;
	}
	
}
