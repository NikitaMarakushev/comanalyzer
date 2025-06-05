public class Example {
    public User getUser(int id) {
        return userRepository.find(id);
    }
}