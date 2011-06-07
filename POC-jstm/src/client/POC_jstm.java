package client;


import jstm4gwt.core.FieldListener;
import jstm4gwt.core.MethodCallback;
import jstm4gwt.core.Share;
import jstm4gwt.core.Site;
import jstm4gwt.core.Transaction;
import jstm4gwt.transports.clientserver.ConnectionInfo;
import transport.GwtClient;
import client.generated.Form;
import client.generated.FormObjectModel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class POC_jstm implements EntryPoint {
	
	private Share share;

    private Form form;

    private Button commit, abort;

    private Transaction activeTransaction;

    public void onModuleLoad() {
        // Register our simple object model

        Site.getLocal().registerObjectModel(new FormObjectModel());

        // Create a JSTM client transport to communicate with the client.

        GwtClient client = new GwtClient();

        client.beginConnect(new MethodCallback() {

            public void onResult(Object obj) {
                // When connected, retrieve the share added by the server

                ConnectionInfo connection = (ConnectionInfo) obj;
                share = (Share) connection.getServerAndClients().getOpenShares().toArray()[0];

                // The server has put a form in this share. This form object is
                // in the share so it is replicated

                form = (Form) share.toArray()[0];

                start();
            }

            public void onTransactionAborted() {
            }

            public void onException(String s) {
            }
        });

        // If a transaction aborts without a reason assigned, it is due to a
        // conflict.

        Site.getLocal().addSiteListener(new Site.Listener() {

            public void onCommitted(Transaction transaction) {
                if (transaction == activeTransaction)
                    setActiveTransaction(null);
            }

            public void onAborted(Transaction transaction) {
                if (transaction == activeTransaction)
                    setActiveTransaction(null);

                if (transaction.getAbortReason() == null)
                    Window.alert("A conflict occurred with changes made by another user.");
            }
        });
    }

    private void start() {
        // For each field of our form, add a label and a TextBox. This will be
        // our poor man's editable grid.

        for (int i = 0; i < Form.FIELD_COUNT; i++) {
            Label label = new Label(Form.getFieldNameStatic(i));
            RootPanel.get().add(label);

            final TextBox textBox = new TextBox();
            textBox.setText((String) form.get(i));
            final int index = i;

            // If the TextBox changes, start a transaction and copy the new
            // value in the replicated form object.

            textBox.addKeyboardListener(new KeyboardListener() {

                public void onKeyDown(Widget arg0, char arg1, int arg2) {
                }

                public void onKeyPress(Widget arg0, char arg1, int arg2) {
                }

                public void onKeyUp(Widget arg0, char arg1, int arg2) {
                    if (!textBox.getText().equals(form.get(index))) {
                        // If the TextBox has been modified, create a
                        // transaction and copy the new value to the replicated
                        // Form instance

                        if (activeTransaction == null) {
                            setActiveTransaction(Site.getLocal().startTransaction());

                            // Do a first read of the field to detect conflicts.
                            // A transaction becomes invalid only if it reads
                            // data and this data changes before it can commit.
                            form.get(index);
                        }

                        form.set(index, textBox.getText());
                    }
                }
            });

            // If someone else has modified the replicated form object, this
            // listener will be called. Update the TextBox.

            form.addListener(new FieldListener() {

                public void onChange(Transaction transaction, int i) {
                    if (i == index)
                        textBox.setText((String) form.get(i));
                }
            });

            // If a transaction is aborted, the form object return to its
            // previous state, so update the TextBox from the form

            Site.getLocal().addSiteListener(new Site.Listener() {

                public void onCommitted(Transaction transaction) {
                }

                public void onAborted(Transaction transaction) {
                    textBox.setText((String) form.get(index));
                }
            });

            RootPanel.get().add(textBox);
        }

        commit = new Button("Commit");

        commit.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                activeTransaction.beginCommit(null);
            }
        });

        RootPanel.get().add(commit);

        abort = new Button("Undo");

        abort.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                // Abort with a reason so we can recognize aborts due to
                // conflicts
                activeTransaction.abort("User action");
            }
        });

        RootPanel.get().add(abort);

        setActiveTransaction(null);
    }

    private void setActiveTransaction(Transaction value) {
        activeTransaction = value;
        commit.setEnabled(activeTransaction != null);
        abort.setEnabled(activeTransaction != null);
    }
}
