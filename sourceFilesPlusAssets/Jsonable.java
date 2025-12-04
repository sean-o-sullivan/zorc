package sourceFilesPlusAssets;

public interface Jsonable {
    public String toJson();
    public void fromJson(String jsonString);
}