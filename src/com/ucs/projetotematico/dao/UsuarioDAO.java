package com.ucs.projetotematico.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ucs.projetotematico.entity.Usuario;

public class UsuarioDAO extends ModelDao<Usuario> {
	private RestricaoDAO restricaoDAO;

	public UsuarioDAO(Connection connection) {
		super.setConnection(connection);
		super.setModel(new Usuario());
		this.restricaoDAO = new RestricaoDAO(connection);
	}

	@Override
	public List<Usuario> findAll() {
		final List<Usuario> receitas = new ArrayList<Usuario>();

		super.findAll(rs -> {
			final Usuario receita = convertResultSet(rs);
			receitas.add(receita);

		});

		return receitas;
	}

	@Override
	public Usuario findById(Integer id) {
		final Usuario model = new Usuario();

		super.findById((rs) -> {
			final Usuario receita = convertResultSet(rs);

			model.setId(receita.getId());
			model.setNome(receita.getNome());
			model.setSenha(receita.getSenha());
			model.setRestricao(receita.getRestricao());

		}, id);
		return model;
	}

	@Override
	public boolean remove(Integer id) {
		return super.remove(id);
	}

	@Override
	public void saveOrUpdate(Usuario model) {

		String sql = "insert into " + getModel().getTableName();

		sql = sql.concat(" ( ");
		if (model.getRestricao() != null) {
			sql = sql.concat(" nome,senha,id_restricao");
		} else {
			sql = sql.concat(" nome,senha");
		}

		sql = sql.concat(" ) ");

		sql = sql.concat(" values ");

		sql = sql.concat(" ( ");

		if (model.getRestricao() != null) {
			sql = sql.concat("?,?,?");
		} else {
			sql = sql.concat("?,?");
		}

		sql = sql.concat(" ) ");

		try {

			final Connection openConnection = super.getConnection();

			final PreparedStatement prepareStatement = openConnection.prepareStatement(sql);
			prepareStatement.setString(1, model.getNome());
			prepareStatement.setString(2, model.getSenha());
			if (model.getRestricao() != null) {
				prepareStatement.setInt(3, model.getRestricao().getId());
			}

			prepareStatement.executeUpdate();

			prepareStatement.close();
			openConnection.close();

		} catch (final SQLException se) {
			System.out.println("Não foi possível conectar ao Banco de Dados");
			se.printStackTrace();
		}

	}

	@Override
	public List<Usuario> findLike(Usuario model) {
		final Map<String, String> map = new HashMap<String, String>();
		final List<Usuario> receitas = new ArrayList<Usuario>();

		map.put("id", null);

		if (model.getId() != null) {
			map.put("id", model.getId().toString());
		}

		map.put("nome", model.getNome());
		map.put("senha", model.getSenha());

		super.findLike(map, rs -> {
			final Usuario receita = convertResultSet(rs);
			receitas.add(receita);
		});

		return receitas;
	}

	public Usuario find(Usuario model) {
		final Map<String, String> map = new HashMap<String, String>();
		final Usuario retorno = new Usuario();

		map.put("id", null);

		if (model.getId() != null) {
			map.put("id", model.getId().toString());
		}

		map.put("nome", model.getNome());
		map.put("senha", model.getSenha());

		super.findByString(map, rs -> {
			final Usuario receita = convertResultSet(rs);
			retorno.setId(receita.getId());
			retorno.setNome(receita.getNome());
			retorno.setSenha(receita.getSenha());
			retorno.setRestricao(receita.getRestricao());
		});

		return retorno;
	}

	@Override
	public Usuario convertResultSet(ResultSet resultSet) {
		final Usuario model = new Usuario();
		try {
			final int idRestricao = resultSet.getInt("id_restricao");

			model.setId(resultSet.getInt("id"));
			model.setNome(resultSet.getString("nome"));
			model.setSenha(resultSet.getString("senha"));
			model.setRestricao(getRestricaoDAO().findById(idRestricao));

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return model;
	}

	public RestricaoDAO getRestricaoDAO() {
		return restricaoDAO;
	}

	public void setRestricaoDAO(RestricaoDAO restricaoDAO) {
		this.restricaoDAO = restricaoDAO;
	}

}
