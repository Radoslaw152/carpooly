package bg.fmi.spring.course.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import bg.fmi.spring.course.dao.PostRepository;
import bg.fmi.spring.course.exceptions.EntityNotFoundException;
import bg.fmi.spring.course.models.Post;
import bg.fmi.spring.course.models.User;
import bg.fmi.spring.course.services.PostService;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getAllPostsByUser(User user) {
        return postRepository.findAll()
                .stream()
                .filter(post -> post.getAuthor().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public Post addPost(Post post) {
        return postRepository.insert(post);
    }

    @Override
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post getPost(String id) {
        return postRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There is no entity with %s ID", id)));
    }

    @Override
    public Post deletePost(String id) {
        Post post = getPost(id);
        postRepository.deleteById(id);
        return post;
    }
}
