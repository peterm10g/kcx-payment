package com.lsh.payment.core.util.ftp;

import java.io.Serializable;


public class SftpModel implements Serializable {

	/** */
	private static final long serialVersionUID = -7831943433985013087L;

	/** sFTP IP */
	private String sftpIp;

	/** sFTP 端口 */
	private Integer sftpPort;

	/** sFTP 账号 */
	private String sftpUsername;

	/** sFTP 密码 */
	private String sftpPassword;

	/** sFTP 路径 */
	private String sftpPath;

	/** s文件名 */
	private String sftpFileName;


	public SftpModel(String sftpIp, Integer sftpPort, String sftpUsername, String sftpPassword, String sftpPath, String sftpFileName) {
		this.sftpIp = sftpIp;
		this.sftpPort = sftpPort;
		this.sftpUsername = sftpUsername;
		this.sftpPassword = sftpPassword;
		this.sftpPath = sftpPath;
		this.sftpFileName = sftpFileName;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getSftpIp() {
		return sftpIp;
	}

	public void setSftpIp(String sftpIp) {
		this.sftpIp = sftpIp;
	}

	public Integer getSftpPort() {
		return sftpPort;
	}

	public void setSftpPort(Integer sftpPort) {
		this.sftpPort = sftpPort;
	}

	public String getSftpUsername() {
		return sftpUsername;
	}

	public void setSftpUsername(String sftpUsername) {
		this.sftpUsername = sftpUsername;
	}

	public String getSftpPassword() {
		return sftpPassword;
	}

	public void setSftpPassword(String sftpPassword) {
		this.sftpPassword = sftpPassword;
	}

	public String getSftpPath() {
		return sftpPath;
	}

	public void setSftpPath(String sftpPath) {
		this.sftpPath = sftpPath;
	}

	public String getSftpFileName() {
		return sftpFileName;
	}

	public void setSftpFileName(String sftpFileName) {
		this.sftpFileName = sftpFileName;
	}
}
