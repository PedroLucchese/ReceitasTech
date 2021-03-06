package com.ucs.projetotematico.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ucs.projetotematico.dao.ReceitaDAO;
import com.ucs.projetotematico.dao.RestricaoIngredienteDAO;
import com.ucs.projetotematico.entity.Receita;
import com.ucs.projetotematico.entity.ReceitaIngrediente;
import com.ucs.projetotematico.entity.RestricaoIngrediente;
import com.ucs.projetotematico.entity.Usuario;

public class PesquisaView extends JFrame implements ActionListener {
	private Connection connection;

	private final List<ReceitaIngrediente> lista;
	private Usuario usuario;

	private JButton bVolta;
	private JPanel fundo, botoes, campos;

	List<String> columns = new ArrayList<String>();
	List<String[]> values = new ArrayList<String[]>();

	private JTextField tIngredientes;

	private JTable table;

	private void init() {

		columns.add("Receitas");

		final List<Receita> carregaResultado = carregaResultado();

		carregaResultado.forEach(e -> {
			values.add(new String[] { e.getNome() });
		});

		final TableModel tableModel = new DefaultTableModel(values.toArray(new Object[][] {}), columns.toArray());

		table = new JTable(tableModel) {
			@Override
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				return false;
			}
		};

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				final JTable table = (JTable) mouseEvent.getSource();
				final Point point = mouseEvent.getPoint();
				final int row = table.rowAtPoint(point);
				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {

					final ReceitaView receitaView = new ReceitaView(carregaResultado, table.getSelectedRow(), usuario, connection);
				}
			}
		});
		this.setTitle("Resultado da pesquisa");
		this.setSize(500, 200);

		bVolta = new JButton("Voltar");
		bVolta.addActionListener(this);

		campos = new JPanel(new GridLayout(4, 2));
		fundo = new JPanel(new BorderLayout());
		botoes = new JPanel(new FlowLayout());

		campos.add(new JLabel("Resultado da pesquisa:"));

		botoes.add(bVolta);

		fundo.add(campos, BorderLayout.NORTH);
		fundo.add(new JScrollPane(table), BorderLayout.CENTER);
		fundo.add(botoes, BorderLayout.SOUTH);

		this.getContentPane().add(fundo);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		this.setVisible(true);

	}

	public List<Receita> carregaResultado() {
		final RestricaoIngredienteDAO restricaoIngredienteDAO = new RestricaoIngredienteDAO(connection);
		final RestricaoIngrediente filter = new RestricaoIngrediente();
		filter.setIdRestricao(usuario.getRestricao().getId());

		final List<RestricaoIngrediente> findLike = restricaoIngredienteDAO.findLike(filter);

		final List<Integer> idReceitas = lista.stream().map(p -> p.getReceita().getId()).distinct().collect(Collectors.toList());
		List<Receita> collect = new ArrayList<Receita>();

		final ReceitaDAO receitaDAO = new ReceitaDAO(connection);

		for (final Integer i : idReceitas) {
			collect.add(receitaDAO.findById(i));
		}

		collect = collect.stream().filter((f) -> {

			if (f.getReceitaIngredientes().stream().anyMatch(a -> findLike.stream().anyMatch(aM -> aM.getIdIngrediente().equals(a.getIngrediente().getId())))) {
				return false;
			}

			return true;
		}).collect(Collectors.toList());

		return collect;
	}

	public PesquisaView() {
		this.init();
		this.lista = new ArrayList<ReceitaIngrediente>();
	}

	public PesquisaView(List<ReceitaIngrediente> lista, Usuario usuario, Connection connection) {
		this.connection = connection;
		this.lista = lista;
		this.usuario = usuario;
		this.init();
	}

	public static void main(String[] args) {

		final PesquisaView pg5 = new PesquisaView();
		// pg5.init();

	}

	private void acaoVoltar() {

		new NavegaView(usuario, connection).setVisible(true);
		this.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(bVolta)) {
			this.acaoVoltar();
		}

	}

}
