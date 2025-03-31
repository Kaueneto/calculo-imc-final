package org.imc.calculoimc.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.imc.calculoimc.model.entity.Pessoa;
import org.imc.calculoimc.utils.idgenerator;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    List<Pessoa> listPessoas;

    @FXML
    public TableView<Pessoa> tabelaPessoas;
    @FXML
    public TableColumn<Pessoa, String> colunanome;
    @FXML
    public TableColumn<Pessoa, String> colunaid;
    @FXML
    public TableColumn<Pessoa, Float> colunaaltura;
    @FXML
    public TableColumn<Pessoa, Float> colunaimc;
    @FXML
    public TableColumn<Pessoa, String> colunaclassifica;


    @FXML
    public TextField txtnome;

    @FXML
    public TextField txtpeso;
    @FXML
    public TextField txtaltura;
    @FXML
    Button btncalcularimc;
    @FXML
    Button btnsalvar;
    @FXML
    Button btnnovo;
    @FXML
    Button btncarregardados;
    @FXML
    Label lblcalculo;
    @FXML
    Label lbltpimc;
    public Pessoa pessoa;


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        this.listPessoas = new ArrayList<>();

        colunaid.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunanome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaaltura.setCellValueFactory(new PropertyValueFactory<>("altura"));
        colunaimc.setCellValueFactory(new PropertyValueFactory<>("imc"));
        colunaclassifica.setCellValueFactory(new PropertyValueFactory<>("classificacao"));
        tabelaPessoas.setItems(FXCollections.observableArrayList(listPessoas));


        this.btncalcularimc.setDisable(true);
        this.btnnovo.setDisable(false);
        this.btnsalvar.setDisable(true);
        this.btncarregardados.setDisable(false);
        this.pessoa = new Pessoa();
        this.listPessoas = new ArrayList<Pessoa>();
        this.txtnome.setDisable(true);
        this.txtaltura.setDisable(true);
        this.txtpeso.setDisable(true);

    }
    public Pessoa lerformulario(){
        this.pessoa.setNome(this.txtnome.getText());
        this.pessoa.setAltura(Float.parseFloat(this.txtaltura.getText()));
        this.pessoa.setPeso(Float.parseFloat(this.txtpeso.getText() ));
        System.out.println(this.pessoa.toString());
        return this.pessoa;
    }
    public String classificarimc(float imc) {
        if (imc < 18.5) {
            return "Abaixo do Peso";
        } else if (imc >= 18.5 && imc < 24.9) {
            return "Peso Normal";
        } else if (imc >= 25 && imc < 29.9) {
            return "Sobrepeso";
        } else if (imc >= 30 && imc < 34.9) {
            return "Obesidade Grau 1";
        } else if (imc >= 35 && imc < 39.9) {
            return "Obesidade Grau 2";
        } else { // imc >= 40
            return "Obesidade Grau 3";
        }
    }


    @FXML
    public void onbtncalcularimc() {
        lerformulario(); // Lê os dados do formulário
        float peso = this.pessoa.getPeso();
        float altura = this.pessoa.getAltura();

        this.btncalcularimc.setDisable(true);


        if (altura > 0) { // Prevenir divisão por zero
            float imc = peso / (altura * altura);
            this.pessoa.setImc(imc);
            this.pessoa.setClassificacao(classificarimc(imc));

            // Exibir na interface
            this.lblcalculo.setText(String.format("%.2f", imc));
            this.lbltpimc.setText(this.pessoa.getClassificacao());

            this.btnsalvar.setDisable(false);

        } else {
            System.out.println("Altura inválida!");
        }
    }
    @FXML
    public void onbtnsalvar() {

        this.btncalcularimc.setDisable(true);

        lerformulario();
        listPessoas.add(new Pessoa(
                idgenerator.generateID(),

                pessoa.getNome(),
                pessoa.getAltura(),
                pessoa.getPeso(),
                pessoa.getImc(),
                pessoa.getClassificacao()
        ));


        tabelaPessoas.setItems(FXCollections.observableArrayList(listPessoas));
        salvapessoa(); //ESSA PARTE É QUE PEGA OS DADOS E JOGA DENTRO DO TXT caso pergunte

        System.out.println("save hein");

        this.btnnovo.setDisable(false);

        this.btncarregardados.setDisable(false);
        this.btnsalvar.setDisable(true);

        this.txtnome.setText("");
        this.txtaltura.setText("");
        this.txtpeso.setText("");

        this.txtnome.setDisable(true);
        this.txtaltura.setDisable(true);
        this.txtpeso.setDisable(true);
    }


    public void salvapessoa() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/org/imc/calculoimc/utils/dadosimc.txt", true))) {
            for (Pessoa p : listPessoas) {
                writer.write(p.getId() + "," + p.getNome() + "," + p.getAltura() + "," + p.getPeso() + "," + p.getImc() + "," + p.getClassificacao());
                writer.newLine();
            }
            System.out.println("Dados salvos no arquivo pessoa.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }







    @FXML
    public void onbtncarregardados() {
        System.out.println("Carregando dados...");

        String caminhoArquivo = "src/main/java/org/imc/calculoimc/utils/dadosimc.txt";

        listPessoas.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ( (linha = reader.readLine()) != null) {


                try {
                    String[] dados = linha.split(",");
                    if (dados.length == 6) {

                        Pessoa p = new Pessoa(
                                Long.parseLong(dados[0]), // pega ID
                                dados[1], // Nome
                                Float.parseFloat(dados[2]), // pega Altura
                                Float.parseFloat(dados[3]), // pega Peso
                                Float.parseFloat(dados[4]), // pega o IMC
                                dados[5] //pega a classificacao
                        );
                        listPessoas.add(p);

                    } else {
                        System.out.println("Linha inválida no arquivo, algo esta faltando: " + linha);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Erro ao converter dados numéricos na linha: " + linha);
                }
            }

            tabelaPessoas.setItems(FXCollections.observableArrayList(listPessoas));
            System.out.println("Dados carregados do arquivo dadosimc.txt.");
        } catch (IOException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        }
    }



    @FXML
    public void onbtnnovo() {
        this.txtnome.setDisable(false);
        this.txtaltura.setDisable(false);
        this.txtpeso.setDisable(false);

        this.btncalcularimc.setDisable(false);
        System.out.println("Novo calculo - botao 'novo' pressionado");
        this.pessoa = new Pessoa();
       // this.pessoa.setId ( idgenerator.generateID () ) ;
        //limparfomulario();
        //  CarregarDadosDoArquivo();
        this.btnnovo.setDisable(true);

    }
    public void limparfomulario(){
        this.txtnome.setText("");
        this.txtaltura.setText("");
        this.txtpeso.setText("");





    }
    public void CarregarDadosDoArquivo() {
        String caminhoArquivo = "src/main/java/org/imc/calculoimc/utils/dadosimc.txt";
        listPessoas.clear(); // Limpar lista antes de carregar
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                try {
                    String[] dados = linha.split(",");
                    Pessoa p = new Pessoa(
                            Long.parseLong(dados[0]), // ID
                            dados[1], // Nome
                            Float.parseFloat(dados[2]), // Altura
                            Float.parseFloat(dados[3]), // Peso
                            Float.parseFloat(dados[4]), // IMC
                            dados[5]  // Classificação
                    );
                    listPessoas.add(p);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Erro ao processar linha: " + linha);
                }
            }
            tabelaPessoas.setItems(FXCollections.observableArrayList(listPessoas));
            System.out.println("Dados carregados do arquivo com sucesso");
        } catch (IOException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        }
    }

    public void onbtnlimpar(){
        System.out.println("limpar - botao limpar");
        this.txtnome.setText("");
        this.txtaltura.setText("");
        this.txtpeso.setText("");
        this.btnnovo.setDisable(false);
        this.btnsalvar.setDisable(true );
    }





}
// teste