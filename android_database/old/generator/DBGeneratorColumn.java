package com.ja.generator;

/**
 * A class that represents a sqlite database colunm
 * properties
 *
 * @author Joseph Acosta
 */
public class DBGeneratorColumn {

    private String name;
    private String type;
    private boolean nullable;
    private int version;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}
}
