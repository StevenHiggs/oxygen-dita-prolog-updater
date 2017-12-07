package com.oxygenxml.prolog.updater.prolog.content;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.oxygenxml.prolog.updater.dita.editor.DocumentType;
import com.oxygenxml.prolog.updater.tags.OptionKeys;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Test the content generated for the DITA topic according to setting.
 */
@RunWith(PowerMockRunner.class)
public class PrologContentCreatorTopicTest extends TestCase {
  private static final String AUTHOR_NAME = "name";
  private static final String LOCAL_DATE = "2017-12-04"; 
  private static final String TRUE_VALUE = String.valueOf(true);
  private static final String FALSE_VALUE = String.valueOf(false);
  
  private WSOptionsStorage wsOptionsStorage;
  
  @Override
  protected void setUp() throws Exception {
    PluginWorkspace pluginWorkspace = Mockito.mock(PluginWorkspace.class);
    wsOptionsStorage = Mockito.mock(WSOptionsStorage.class);
    Mockito.when(pluginWorkspace.getOptionsStorage()).thenReturn(wsOptionsStorage);
    PowerMockito.mockStatic(PluginWorkspaceProvider.class);
    PowerMockito.when(PluginWorkspaceProvider.getPluginWorkspace()).thenReturn(pluginWorkspace);
  }
  
  
  
  
  
  /**
   * <p><b>Description:</b> Test the functionality when TOPIC_ENABLE_UPDATE_ON_SAVE option is false.</p>
   *
   */
  @PrepareForTest({ PluginWorkspaceProvider.class })
  @Test
  public void testUpdateDisable() {
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME);
    
    //Get the prolog according to settings when document is new.
    String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.TOPIC);
    
    //The prolog fragment is not generated.
    assertNull(prologFragment);
    
    //Get the prolog according to settings when document isn't new.
    prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.TOPIC);
    
    //The prolog fragment is not generated.
    assertNull(prologFragment);
  }

  
  /**
   * <p><b>Description:</b> Test the functionality when TOPIC_ENABLE_UPDATE_ON_SAVE option is false and the rest of options are false</p>
   *
   */
  @PrepareForTest({ PluginWorkspaceProvider.class })
  @Test
  public void testOptionsFalse() {
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATOR, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATED_DATE, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    
    PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME);
    
    //Get the prolog in new document.
    String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.TOPIC);
    
    //Check the generated prolog fragment.
    assertNull(prologFragment);
    
    //Get the prolog when the document isn't new.
     prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.TOPIC);
    
    //Check the generated prolog fragment.
    assertNull(prologFragment);
  }
  
  
  
  /**
   * <p><b>Description:</b> Test the functionality when TOPIC_SET_CREATOR option is set true</p>
   *
   */
  @PrepareForTest({ PluginWorkspaceProvider.class })
  @Test
  public void testSetCreator() {
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATOR, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATED_DATE, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    
    // When TOPIC_SET_CREATOR is false 
    PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME);
    
    //Get the prolog in new document.
    String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.TOPIC);
    
    //Check the generated prolog fragment.
    assertEquals("<prolog><author type=\"creator\">name</author></prolog>", prologFragment);
    
    //Get the prolog when document isn't new.
    prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.TOPIC);
    
    assertNull(prologFragment);
  }
  
  /**
   * <p><b>Description:</b> Test the functionality when TOPIC_SET_CREATED_DATE options is set true</p>
   *
   */
  @PrepareForTest({ PluginWorkspaceProvider.class })
  @Test
  public void testSetCreatedDate() {
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATOR, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATED_DATE, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    
    // When TOPIC_SET_CREATOR is false 
    PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME) {
      @Override
      protected String createLocalDate() {
        return LOCAL_DATE;
      }
    };
    
    //Get the prolog when document is new.
    String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.TOPIC);
    
    //Check the generated prolog fragment.
    assertEquals("<prolog><critdates><created date=\"2017-12-04\"/></critdates></prolog>", prologFragment);
    
    //Get the prolog when document isn't new.
     prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.TOPIC);
    
    //Check the generated prolog fragment.
    assertNull(prologFragment);
  }
  
  /**
   * <p><b>Description:</b> Test the functionality when TOPIC_UPDATE_CONTRIBUTOR options is set true</p>
   *
   */
  @PrepareForTest({ PluginWorkspaceProvider.class })
  @Test
  public void testUpdateContributor() {
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATOR, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATED_DATE, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    
    // When TOPIC_SET_CREATOR is false 
    PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME);
    
    //Get the prolog when document is new.
    String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.TOPIC);
    
    //Check the generated prolog fragment.
    assertNull(prologFragment);
    
    //Get the prolog when document isn't new.
     prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.TOPIC);
    
    //Check the generated prolog fragment.
     assertEquals("<prolog><author type=\"contributor\">name</author></prolog>", prologFragment);
    
  }
  
  /**
   * <p><b>Description:</b> Test the functionality when TOPIC_UPDATE_REVISED_DATES options is set true</p>
   *
   */
  @PrepareForTest({ PluginWorkspaceProvider.class })
  @Test
  public void testUpdateRevised() {
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATOR, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_SET_CREATED_DATE, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR, TRUE_VALUE)).thenReturn(FALSE_VALUE);
    Mockito.when(wsOptionsStorage.getOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, TRUE_VALUE)).thenReturn(TRUE_VALUE);
    
    // When TOPIC_SET_CREATOR is false 
    PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME) {
      @Override
      protected String createLocalDate() {
        return LOCAL_DATE;
      }
    };
    
    //Get the prolog when document is new.
    String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.TOPIC);
    
    //Check the generated prolog fragment.
    assertNull(prologFragment);
    
    //Get the prolog when document isn't new.
     prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.TOPIC);
     assertEquals("<prolog><critdates><!--name--><revised modified=\"2017-12-04\"/></critdates></prolog>", prologFragment);
    
    //Check the generated prolog fragment.
    
  }
}
