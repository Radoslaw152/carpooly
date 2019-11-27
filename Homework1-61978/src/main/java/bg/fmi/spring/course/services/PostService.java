package bg.fmi.spring.course.services;

import java.util.List;

import bg.fmi.spring.course.models.Post;
import bg.fmi.spring.course.models.User;

public interface PostService {
    List<Post> getAllPosts();

    List<Post> getAllPostsByUser(User user);

    Post addPost(Post post);

    Post updatePost(Post post);

    Post getPost(String id);

    Post deletePost(String id);
}
