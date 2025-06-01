public class Example {
    // Gets user by ID
    public User getUser(int id) {
        return userRepository.find(id);
    }
}