package herramientas;

public class ExpresionesRegulares {
    private ExpresionesRegulares() {
    }

    public static final String TLF = "^\\d{9}$";
    public static final String EMAIL = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String CONTRASENA = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
    public static final String DNI = "\\d{8}[A-HJ-NP-TV-Z]";

}
