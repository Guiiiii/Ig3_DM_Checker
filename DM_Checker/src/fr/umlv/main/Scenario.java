package fr.umlv.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import fr.umlv.IHM.IHM;
import fr.umlv.archive.ArchiveOptionChecker;
import fr.umlv.archive.ZipFile;
import fr.umlv.junit.Junit;
import fr.umlv.util.Messages;
import fr.umlv.util.Options;
import fr.umlv.util.Reports;
/**
 * 
 * 
 * 
 * 
 * @author Meriadoc
 *
 */
public class Scenario {
	
	private final Options options;
	private final Reports reports;
	private final ArchiveOptionChecker optCheck;
	private Junit junit;
	
	public Scenario(Options opt, Reports reports){
		options=opt;
		this.reports=reports;
		optCheck = new ZipFile();
		junit = new Junit();
	}
	
	
	//echec option force : pas extratiction et archive refus�
		private boolean optionForceRefused(String option, String param){
			System.err.println(Messages.getOutputString(option, param));
			return false;
		}
		private void optionRefused(String option, String param){
			System.out.println(Messages.getOutputString(option, param));
		}
	
	
	//On ne dezip pas sur l'option -1
	/**
	 * Check les options sur une archive return booleen pour savoir si on dezip
	 * methode priv� on ne fait pas de javadoc
	 * 
	 * @param path
	 */
	public boolean checkOptionsArchive(String path){
		//test one top
		if(options.isOneTop()||options.isForceOneTop()){
			if (!optCheck.oneTop(path))
				if (options.isForceOneTop()){
					return optionForceRefused("onetop", "");
					//sortie car option refus�
				}
				else{
					optionRefused("onetop", "");
				}
		}
		//check opt I et x
		try {
			for(String s : options.getForbidden()){
				if (optCheck.existe(path, s))
					optionRefused("i", s);
			}
			
			for(String s : options.getForceinterdit()){
				if (optCheck.existe(path,s))
					return optionForceRefused("i", s);
			}
			
			for(String s : options.getBe()){
				if (!optCheck.existe(path,s))
					optionRefused("i", s);
			}
			
			for(String s : options.getForceBe()){
				if (!optCheck.existe(path,s))
					return optionForceRefused("i", s);
			}
		} catch (IOException e) {
			System.err.println("arret innatendu du programme");
			e.printStackTrace();
		}
		//endswith
		for(String s : options.getEndWith()){
			if (optCheck.endsWith(path, s)){
				optionRefused("e",s );
			}
		}
		
		for(String s : options.getForceEndsWith()){
			if (optCheck.endsWith(path, s)){
				return optionForceRefused("e",s );
			}
		}
		
		
		//startwiths
		for(String s : options.getEndWith()){
			if (!optCheck.beginsWith(path, s))
				optionRefused("b",s );
		}
		
		for(String s : options.getForceBeginsWith()){
			if (optCheck.endsWith(path, s)){
				return optionForceRefused("b",s );
			}
		}
		if (options.isVerbose()){
			System.out.println(path+" OK");
		}
		return true;
	}
	
	public void checkArchiveSerial(String path){
		//init : extrait l'archive d'archive
		ArrayList<String> paths;
		Objects.requireNonNull(paths = optCheck.getPathArchive(path));
		if(paths.size()==0){
			System.err.println("pas d'archive d'archive !");
			return;
		}
		optCheck.extract(path, paths.get(0));
		paths.remove(0);
		//lance le traitement
		for(String p : paths){
			try {
				if((checkOptionsArchive(p))&&(optCheck.isValid(p))){
					//TODO verif les path
					optCheck.extract(p, options.getDestination());
				}
			} catch (IOException e) {
				System.err.println("Une erreur est survenue lors de la lecture du fichier "+p);
			}
		}
	}
	
	public void jUnitIHM(String jUnitPath){
		junit.execute(options.getJUnitPath());
	}
	
	public ArrayList<String> initIHM(){
		String path=options.getSource();
		ArrayList<String> p = optCheck.getPathArchive(path);
		optCheck.extract(path, p.get(0));
		p.remove(0);
		return p;
	}
	public void exctratIHM(String path){
		
	}
	
	public void jUnitTesting(String path){
		//on extrait les fichier valide
		checkArchiveSerial(path);
		//on lance les jUnits
		junit.execute(options.getJUnitPath());
	}
	
	
	private boolean ihmMode(Scenario sc){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame fenetre = new IHM(sc);
				fenetre.setVisible(true);
			}
		});
		return false;
	}
	/**
	 * suprimmer les zip a la fin !!
	 */
	public void start(){
		optCheck.setVerbose(options.isVerbose());
		//check si il y a bien une source
		if (options.getSource().compareTo("")==0){
			System.err.println("Dossier source invalide");
			return;
		}
		//lance un sc�nation
		switch(options.getMode()){
			case 1 : checkOptionsArchive(options.getSource());break;
			case 2 : checkArchiveSerial(options.getSource());break;
			case 3 : jUnitTesting(options.getSource());break;
			case 4 : ihmMode(this);break;
			default : System.err.println("Erreur : pas de scenario associe");
		}
	}
}
