package com.ucs.projetotematico.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import com.ucs.projetotematico.view.InicialView;

public class Main {

	public static Connection openConnection() {

		Connection connection = null;

		final Properties prop = loadFile();

		final String driver = prop.getProperty("driver");
		final String url = prop.getProperty("url");
		final String user = prop.getProperty("user");
		final String pwd = prop.getProperty("pwd");

		try {

			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, pwd);
		} catch (final ClassNotFoundException cnfe) {
			System.out.println("Driver JDBC não encontrado");
		} catch (final SQLException se) {
			System.out.println("Falha na conexão " + se.getMessage());
		}

		return connection;
	}

	public static Properties loadFile() {
		final Properties prop = new Properties();
		try (InputStream input = new FileInputStream("connection.properties")) {
			prop.load(input);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}

	public static void main(String[] args) {
		final Connection connection = openConnection();

		try {
			for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (final UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (final ClassNotFoundException e) {
			// handle exception
		} catch (final InstantiationException e) {
			// handle exception
		} catch (final IllegalAccessException e) {
			// handle exception
		}
		final InicialView i = new InicialView(connection);

	}

}
