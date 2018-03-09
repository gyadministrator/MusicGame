package bean;

public class Apk {
	private Integer id;

	private Integer versioncode;

	private String url;

	private String apkname;

	private String content;

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Apk [id=" + id + ", versioncode=" + versioncode + ", url="
				+ url + ", apkname=" + apkname + ", content=" + content + "]";
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVersioncode() {
		return versioncode;
	}

	public void setVersioncode(Integer versioncode) {
		this.versioncode = versioncode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url == null ? null : url.trim();
	}

	public String getApkname() {
		return apkname;
	}

	public void setApkname(String apkname) {
		this.apkname = apkname == null ? null : apkname.trim();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}
}