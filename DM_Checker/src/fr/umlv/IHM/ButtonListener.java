package fr.umlv.IHM;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ButtonListener extends AbstractAction {

	private IHM ihm;

	public ButtonListener(IHM ihm, String texte) {
		super(texte);

		this.ihm = ihm;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();

		if (src == ihm.getButtonNext()) {
			ihm.incrementIndice();
			ihm.editNameLabelTop();
			ihm.cleanSheet();
			ihm.setReport();
			//arret du precessus
			ihm.stopExe();
			ihm.getButtonRun().setText("Run");
			ihm.setExe();
		} else if (src == ihm.getButtonPrevious()) {
			//add le rapport
			ihm.decrementIndice();
			ihm.editNameLabelTop();
			ihm.cleanSheet();
			ihm.setReport();
			ihm.stopExe();
			//arret du processus
			ihm.setExe();
		} else if (src == ihm.getButtonRun()) {
			// set the button label
			if (ihm.getButtonRun().getText().compareTo("Run") == 0) {
				ihm.getButtonRun().setText("Stop");
				//on lance l'executable
				ihm.launchExe();
			} else {
				ihm.getButtonRun().setText("Run");
				ihm.stopExe();
			}
			
		}

	}
}