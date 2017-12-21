package com.oxygenxml.prolog.updater;

import com.oxygenxml.prolog.updater.dita.editor.DitaEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicAuthorEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicTextEditor;
import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.tags.OptionKeys;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Update the prolog in DITA topics.
 * 
 * @author cosmin_duna
 */
public class DitaUpdater {
  /**
   * Unknown user name value.
   */
  private static final String UNKNOWN = "unknown";
  /**
   * System property of user name.
   */
  private static final String USER_NAME_PROPERTY = "user.name";
  
  /**
   * Update the prolog of the current page from given wsEditor.
   * 
   * @param wsEditor Workspace editor.
   */
  public void updateProlog(WSEditor wsEditor , boolean isNewDocument) {
    //get the currentPage
    WSEditorPage currentPage = wsEditor.getCurrentPage();

    //create a PrologContentCreator
    PrologContentCreator prologContentCreater = new PrologContentCreator(getAuthorName());
    
    DitaEditor ditaEditor = null;
    if (currentPage instanceof WSAuthorEditorPage) {
      //Author page
      WSAuthorEditorPage authorPage = (WSAuthorEditorPage)currentPage;
      ditaEditor = new DitaTopicAuthorEditor(authorPage, prologContentCreater);
    
    } else if (currentPage instanceof WSXMLTextEditorPage) {
      //Text page
      WSXMLTextEditorPage textPage = (WSXMLTextEditorPage)currentPage;
      ditaEditor = new DitaTopicTextEditor(textPage, prologContentCreater);
      
    } else if (currentPage instanceof WSDITAMapEditorPage) {
      //DMM
      WSDITAMapEditorPage mapEditorPage = (WSDITAMapEditorPage)currentPage;
      ditaEditor = new DitaTopicAuthorEditor(mapEditorPage, prologContentCreater);
    }
    
    if(ditaEditor != null){
      ditaEditor.updateProlog(isNewDocument);
    }
  }

  /**
   * @return The author's name. Never <code>null</code>.
   */
  protected String getAuthorName(){
    String toReturn = UNKNOWN;
    
    WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
    String name = System.getProperty(USER_NAME_PROPERTY);
    if(optionsStorage != null) {
      toReturn = optionsStorage.getOption(OptionKeys.AUTHOR_NAME, name);
    }else {
      if(name != null) {
        toReturn = name;
      }
    }
    return toReturn;
  }
}