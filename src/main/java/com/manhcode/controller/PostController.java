package com.manhcode.controller;

import com.manhcode.model.Post;
import com.manhcode.model.PostForm;
import com.manhcode.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("posts")
@PropertySource("classpath:upload_file.properties")
public class PostController {
    @Autowired
    private IPostService postService;

    @GetMapping("")
    public String index(Model model) {
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("post", new Post());
        return "/create";
    }

    @Value("${file-upload}")
    private String upload;

    @PostMapping("/save")
    public String save(PostForm postForm) {
        MultipartFile file = postForm.getImage();
        String fileName = file.getOriginalFilename();
        try {
            FileCopyUtils.copy(file.getBytes(), new File(upload + fileName));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Post post = new Post();
        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent());
        post.setDescription(postForm.getDescription());
        post.setImage(fileName);
        postService.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String update(@PathVariable int id, Model model) {
        model.addAttribute("post", postService.findById(id));
        return "/update";
    }

    @PostMapping("/update")
    public String update(PostForm postForm) {
        MultipartFile file = postForm.getImage();
        String fileName = file.getOriginalFilename();
        try {
            FileCopyUtils.copy(file.getBytes(), new File(upload+fileName));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Post post = new Post();
        post.setId(postForm.getId());
        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent());
        post.setDescription(postForm.getDescription());
        post.setImage(fileName);
        postService.update(post.getId(), post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable int id, Model model) {
        model.addAttribute("post", postService.findById(id));
        return "/delete";
    }

    @PostMapping("/delete")
    public String delete(Post post, RedirectAttributes redirect) {
        postService.remove(post.getId());
        redirect.addFlashAttribute("success", "Removed customer successfully!");
        return "redirect:/posts";
    }

    @GetMapping("/{id}/view")
    public String view(@PathVariable int id, Model model) {
        model.addAttribute("post", postService.findById(id));
        return "/view";
    }
}
