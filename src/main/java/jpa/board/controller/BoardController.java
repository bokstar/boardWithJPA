package jpa.board.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jpa.board.dto.BoardList;
import jpa.board.entity.Board;
import jpa.board.entity.Member;
import jpa.board.persistence.BoardRepository;
import jpa.board.service.BoardService;

@Controller
@RequestMapping("/board")
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private BoardRepository boardRepo;
	
    @GetMapping("")
    public String goBoard(HttpServletRequest request, Model model) {
    	
    	try {
    		String requestPage = request.getParameter("page");
    		int page = StringUtils.isEmpty(requestPage) ? 1 : Integer.parseInt(requestPage);
    		
    		List<BoardList> boardList = boardService.getBoardList(page);
    		List<String> pageList = boardService.getPagination(page);
    		
    		model.addAttribute("boardList", boardList);
    		model.addAttribute("pageList", pageList);
    		model.addAttribute("page", page);
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	
    	return "/board/board";
    }
    
    @GetMapping("/boardContent")
    public String goBoardContent(HttpServletRequest request, Model model) {
    	String seq = StringUtils.isEmpty(request.getParameter("seq")) ? "0" : request.getParameter("seq");
    	String page = StringUtils.isEmpty(request.getParameter("page")) ? "1" : request.getParameter("page");
    	String goPage = "redirect:/board?page="+page;
    	
    	try {
    		Optional<Board> boardOpt = boardService.getBoard(seq);
    		Board board = boardOpt.orElse(null);
    		
    		if(board == null) {
    			return goPage;
    		}
    		
    		Member member = Optional.ofNullable((Member)request.getSession().getAttribute("member")).orElse(new Member());
    		model.addAttribute("writer", member.getId() == board.getMember().getId());
    		model.addAttribute("board", board);
    		model.addAttribute("page", page);
    		
    		goPage = "/board/boardContent";
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return goPage;
    }
    
    @GetMapping("/delete/boardContent")
    public String deleteBoardContent(HttpServletRequest request, Model model) {
    	String page = request.getParameter("page");
    	
    	boardService.deleteBoardContent(request);
    	
    	return "redirect:/board?page="+page;
    }
    
    @GetMapping("/write")
    public String getWriteBoard(HttpServletRequest request, Model model) {
    	String page = StringUtils.isEmpty(request.getParameter("page")) ? "1" : request.getParameter("page");
    	model.addAttribute("page", page);
    	
    	return boardService.getWriteBoard(request);
    }
    
    @PostMapping("/write/boardContent")
    public String postWriteBoard(HttpServletRequest request) {
    	return boardService.postWriteBoard(request);
    }
    
    @GetMapping("/update")
    public String getUpdateBoard(HttpServletRequest request, Model model) {
    	return boardService.setUpdateBoardContent(request, model);
    }
    
    @PostMapping("/update/boardContent")
    public String postUpdateBoard(HttpServletRequest request) {
    	return boardService.updateBoardContent(request);
    }
}
