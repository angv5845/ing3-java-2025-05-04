package Dao;

// import des packages
import Modele.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminProduitDaoImpl implements AdminProduitDao {

    public AdminProduitDaoImpl() {

    }

    @Override

    public ArrayList<Produit> getAll() {
        ArrayList<Produit> listeProduits = new  ArrayList<Produit>();


            //Récupérer la liste des produits de la base de données dans listeProduits

        try {

            Connection connexion = JdbcDataSource.getConnection();;
            Statement statement = connexion.createStatement();

            ResultSet resultats = statement.executeQuery("select * from produit");

            // 	Se déplacer sur le prochain enregistrement : retourne false si la fin est atteinte
            while (resultats.next()) {
                // récupérer les 3 champs de la table produits dans la base de données
                int idProduit = resultats.getInt(1);
                String produitNom = resultats.getString(2);
                String image = resultats.getString(3);
                int idImage = resultats.getInt(4);
                double prix = resultats.getDouble(5);
                int quantite = resultats.getInt(6);
                String description = resultats.getString(7);
                String categorie = resultats.getString(8);


                // instancier un objet de Produit avec ces 3 champs en paramètres
                Produit product = new Produit(idProduit,produitNom,description,quantite,prix,image,categorie,idImage);

                listeProduits.add(product);
            }
        }
        catch (SQLException e) {
            //traitement de l'exception
            e.printStackTrace();
            System.out.println("Extraction de la liste de produits impossible");
        }

        return listeProduits;
    }

    @Override

     //Ajouter un nouveau produit en paramètre dans la base de données

    public void ajouter(Produit product) {
        try {
            // connexion
            Connection connexion = JdbcDataSource.getConnection();
            // Exécution de la requête INSERT INTO de l'objet product en paramètre
            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "insert into produit(nom,image,marque_id,prix,quantite,description,category) values('" + product.getNomProduit() + "','" +
                            product.getImage() + "'," + product.getIdMarque() + "," + product.getPrix() + ",'" + product.getQuantite() + "','"
                            + product.getDescription()+"" + "','"+ product.getCategorie() +"')");
            preparedStatement.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ajout du produit impossible");
        }
    }

    @Override
    public Produit getById(int id) {
        Produit product = null;
        try {
            Connection connexion = JdbcDataSource.getConnection();;
            Statement statement = connexion.createStatement();

            ResultSet resultats = statement.executeQuery("select * from produit where id="+id);

            while (resultats.next()) {

                int idProduit = resultats.getInt(1);
                String produitNom = resultats.getString(2);
                String image = resultats.getString(3);
                int idImage = resultats.getInt(4);
                double prix = resultats.getDouble(5);
                int quantite = resultats.getInt(6);
                String description = resultats.getString(7);
                String categorie = resultats.getString(8);

                // Si l'id du produit est trouvé, l'instancier et sortir de la boucle
                if (id == idProduit) {
                    // instanciation de l'objet de Produit avec ces 3 champs
                    product = new Produit(idProduit,produitNom,description,quantite,prix,image,categorie,idImage);
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Produit non trouvé dans la base de données");
        }
        return product;
    }

    public int getIdByNom(String nomProduit) {
        int idProduit = -1;
        String sql = "SELECT id FROM produit WHERE nom = ?";

        try (Connection conn = JdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomProduit);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idProduit = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idProduit;
    }

    @Override
    public Produit modifier(Produit product) {
        Produit old_produit = getById(product.getIdProduit());
        try {
            Connection connexion = JdbcDataSource.getConnection();

            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "UPDATE produit SET nom='"+product.getNomProduit()+
                            "',prix="+product.getPrix()+",image='"+product.getImage()+
                            "',marque_id="+product.getIdMarque()+",quantite="+product.getQuantite()+
                            ",description='"+product.getDescription()+"',category='"+product.getCategorie()+
                            "' WHERE id="+product.getIdProduit());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Modification du produit impossible");
        }

        return product;
    }

    @Override
    public void supprimer(int idProduct) {
        try {
            Connection connexion = JdbcDataSource.getConnection();

            PreparedStatement preparedStatement = connexion.prepareStatement(
                    "DELETE FROM produit WHERE id="+idProduct);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Suppression du produit impossible");
        }

    }

    public List<Map.Entry<String, Integer>> getTopSellingProducts() {
        Map<String, Integer> topSellingProducts = new HashMap<>();
        String query = "SELECT p.nom, SUM(ep.quantite) as totalVendu " +
                "FROM element_panier ep " +
                "JOIN produit p ON ep.produit_id = p.id " +
                "GROUP BY p.nom " +
                "ORDER BY totalVendu ASC " +
                "LIMIT 10";

        try (Connection conn = JdbcDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                topSellingProducts.put(rs.getString("nom"), rs.getInt("totalVendu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(topSellingProducts.entrySet());
    }


}