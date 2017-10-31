package com.oxygenxml.prolog.updater;

import com.oxygenxml.prolog.updater.dita.editor.DitaTopicAuthorEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicTextEditor;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;

/**
 * Update the prolog in DITA topics.
 * 
 * @author intern4
 *
 */
public class DitaUpdater {
	
	/**
	 * Update the prolog of the current page from given wsEditor.
	 * 
	 * @param wsEditor
	 *          Workspace editor.
	 */
	public void updateProlog(WSEditor wsEditor) {

		//get the currentPage
		WSEditorPage currentPage = wsEditor.getCurrentPage();

		// get the reviewerAuthorName
		String reviewerAuthorName = getAuthorName();
		
		//create a PrologContentCreator
		PrologContentCreater prologContentCreater = new PrologContentCreater(reviewerAuthorName);
		
		DitaTopicEditor ditaEditor = null;
		
		if (currentPage instanceof WSAuthorEditorPage) {
			ditaEditor = new DitaTopicAuthorEditor(currentPage, prologContentCreater);

		} else if (currentPage instanceof WSXMLTextEditorPage) {
			ditaEditor = new DitaTopicTextEditor(currentPage, prologContentCreater);
		}

		if(ditaEditor != null){
			ditaEditor.updateProlog(wsEditor.isNewDocument());
		}
	}

	
	/*
	 * Get the name of the author.
	 */
	protected String getAuthorName(){
	//TODO get the author name
	// ((WSAuthorEditorPage) currentPage).getAuthorAccess().getReviewController().getReviewerAuthorName();
		
//	PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
//	
//	WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
//	if (optionsStorage != null) {
//		String nume = optionsStorage.getOption("change.tracking.author", authorName);
//		System.out.println("author name: " + nume);
//	}
		
		return "test";
	}
	

}