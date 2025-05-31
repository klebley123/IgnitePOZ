package Models;

public class Client {

    private int id;
    private String name;
    private String nip;
    private String phone;
    private String email;

    public Client(int id, String name, String nip, String phone, String email) {
        this.id = id;
        this.name = name;
        this.nip = nip;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNip() {
        return nip;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setNip(String nip){
        this.nip = name;
    }

    public void setPhone(String phone){
        this.phone = name;
    }

    public void setEmail(String email){
        this.email = name;
    }

    @Override
    public String toString() {
        return name + " (" + nip + ")";
    }
}
