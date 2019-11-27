package bg.fmi.spring.course.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import bg.fmi.spring.course.exceptions.InvalidEntityIdException;
import bg.fmi.spring.course.exceptions.WrongAccess;
import bg.fmi.spring.course.models.Post;
import bg.fmi.spring.course.models.User;
import bg.fmi.spring.course.services.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Post>> getAllPosts(UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (currentUser.getRole().equals("blogger")) {
            return ResponseEntity.ok(postService.getAllPostsByUser(currentUser));
        }
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Post> addPost(@RequestBody Post post,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        post.setAuthor(currentUser);
        return ResponseEntity.ok(postService.addPost(post));
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Post> getPost(@PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        Post post = postService.getPost(id);
        if (!currentUser.equals(post.getAuthor()) && !currentUser.getRole().equals("administrator")) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",currentUser.getEmail(),id));
        }
        return ResponseEntity.ok(post);
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Post> updatePost(@RequestBody Post post, @PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (!post.getId().equals(id)) {
            throw new InvalidEntityIdException(
                    String.format("Article ID=%s from path is different from Entity ID=%s", id,
                            post.getId()));
        }
        if (!post.getAuthor().equals(currentUser) && !currentUser.getRole().equals(
                "administrator")) {
            throw new WrongAccess(String.format("User with email=%s cannot update post with ID=%s",currentUser.getEmail(),id));
        }
        return ResponseEntity.ok(postService.updatePost(post));
    }

    @RequestMapping(method = RequestMethod.DELETE,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Post> deletePost(@PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        Post post = postService.getPost(id);
        if (!post.getId().equals(id)) {
            throw new InvalidEntityIdException(
                    String.format("Article ID=%s from path is different from Entity ID=%s", id,
                            post.getId()));
        }
        if (!post.getAuthor().equals(currentUser) && !currentUser.getRole().equals(
                "administrator")) {
            throw new WrongAccess(String.format("User with email=%s cannot delete post with ID=%s",currentUser.getEmail(),id));
        }
        return ResponseEntity.ok(postService.deletePost(id));
    }
}
