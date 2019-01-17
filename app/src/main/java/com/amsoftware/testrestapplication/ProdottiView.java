package com.amsoftware.testrestapplication;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.amsoftware.testrestapplication.entity.ProdottiEntity;
import com.amsoftware.testrestapplication.service.MagazzinoService;


public class ProdottiView extends AppCompatActivity
{
    private TableLayout prodottiTable;
    private TableLayout prodottiTableHeader;
    private TableLayout prodottiTableFooter;
    private String mStoredToken;
    private boolean mRememberMe;

    @Override
    protected void  onResume()
    {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //UTILITY to set the context locale based on the configurat-ion file
        MagazzinoService.configureLocale(this,getBaseContext());

        setContentView(R.layout.activity_prodotti_view);

        setTitle(MagazzinoService.getProdottiActivityTitle(this));

        String requestToken = this.getIntent().getStringExtra(MagazzinoService.getAuthenticationHeader(this));
        if(requestToken!=null)
        {
            mStoredToken = requestToken;
            //the same token can be used for crud operations
        }

        ProdottiEntity[] prodotti = (ProdottiEntity[]) this.getIntent().getSerializableExtra(MagazzinoService.CALL_NAME.REST_PRODOTTI.toString());

        prodottiTable       = (TableLayout) findViewById(R.id.main_table);
        prodottiTableHeader = (TableLayout) findViewById(R.id.header_table);
        prodottiTableFooter = (TableLayout) findViewById(R.id.footer_table);

        int uid = 1;
        int CELL_WIDTH   = getResources().getInteger(R.integer.cellwidth);
        int CELL_PADDING = getResources().getInteger(R.integer.cellpadding);
        TableRow tr_head = buildTableHeader(uid++,Color.parseColor(getResources().getString(R.string.color_header_footer)));
        TableRow tr_foot = buildTableHeader(uid++,Color.parseColor(getResources().getString(R.string.color_header_footer)));
        String[] headers = MagazzinoService.getProdottiHeaders(this);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        final int numColumns = headers.length;

        for(String s: headers)
        {
            addCenterWithBorderColumnToRow(tr_head, isLandscape ? s : (CELL_WIDTH > 0 && s.length() > CELL_WIDTH) ? getRows(s,CELL_WIDTH): s+ "\n", uid++, Color.WHITE,CELL_PADDING,R.drawable.shapelinear);
            addCenterWithBorderColumnToRow(tr_foot, isLandscape ? s : (CELL_WIDTH > 0 && s.length() > CELL_WIDTH) ? getRows(s,CELL_WIDTH): s+ "\n", uid++, Color.WHITE,CELL_PADDING,R.drawable.shapelinear);
        }

        addHeaderRowToTable(tr_head,prodottiTableHeader);
        addHeaderRowToTable(tr_foot,prodottiTableFooter);
        if(prodotti == null || prodotti.length == 0)
        {
            Toast toast = Toast.makeText(ProdottiView.this, MagazzinoService.getInfoEmptyProducts(ProdottiView.this), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();

            TableRow tr_data = buildTableData(true,uid++,Color.WHITE,Color.parseColor(getResources().getString(R.string.color_alternate_data_row)));
            addLeft0dpColumnToRow(tr_data,"",uid++,Color.BLACK,0,CELL_PADDING);
            addLeft0dpColumnToRow(tr_data, "",uid++,Color.BLACK,0,CELL_PADDING);
            addLeft0dpColumnToRow(tr_data, "",uid++,Color.BLACK,0,CELL_PADDING);
            addLeft0dpColumnToRow(tr_data,"",uid++,Color.BLACK,0,CELL_PADDING);
            addLeft0dpColumnToRow(tr_data,"",uid++,Color.BLACK,0,CELL_PADDING);
            addDataRowToTable(tr_data,prodottiTable);
        }
        if(prodotti!=null)
        {

            boolean isOne = false;

            for(ProdottiEntity p : prodotti)
            {
                isOne = isOne ? false : true;

                TableRow tr_data = buildTableData(isOne,uid++,Color.WHITE,Color.parseColor(getResources().getString(R.string.color_alternate_data_row)));
                addLeft0dpColumnToRow(tr_data,""+p.getId(),uid++,Color.BLACK,0,CELL_PADDING);
                addLeft0dpColumnToRow(tr_data, isLandscape ? p.getNome() : p.getNome().length() > CELL_WIDTH ? getRows(p.getNome(),CELL_WIDTH) : p.getNome(),uid++,Color.BLACK,0,CELL_PADDING);
                addLeft0dpColumnToRow(tr_data, isLandscape ? p.getDescrizione() : p.getDescrizione().length() > CELL_WIDTH ? getRows(p.getDescrizione(),CELL_WIDTH): p.getDescrizione(),uid++,Color.BLACK,0,CELL_PADDING);
                addLeft0dpColumnToRow(tr_data,""+p.getQuantita(),uid++,Color.BLACK,0,CELL_PADDING);
                addCurrencyLeft0dpColumnToRow(tr_data,""+p.getPrezzo(),uid++,Color.BLACK,0,CELL_PADDING);
                addDataRowToTable(tr_data,prodottiTable);
            }

        }


        //solve the alignment header/footer - content problem
        prodottiTable.post(new Runnable()
        {
            @Override
            public void run()
            {
                TableRow tr1 = (TableRow) prodottiTable.getChildAt(0);

                if(tr1==null || tr1.getChildAt(0)==null)
                {
                    prodottiTableHeader.setStretchAllColumns(true);
                    prodottiTableFooter.setStretchAllColumns(true);

                    return;
                }

                TableRow tr2 = (TableRow) prodottiTableHeader.getChildAt(0);
                TableRow tr3 = (TableRow) prodottiTableFooter.getChildAt(0);

                int heightH = tr2.getChildAt(0).getMeasuredHeight();

                for(int i = 1; i < numColumns; i++)
                {
                    heightH = heightH >= tr2.getChildAt(i).getMeasuredHeight() ? heightH : tr2.getChildAt(i).getMeasuredHeight();
                }

                for(int i = 0; i < numColumns; i++)
                {
                    TextView dataCell   = (TextView) tr1.getChildAt(i);
                    TextView headerCell = (TextView) tr2.getChildAt(i);
                    TextView footerCell = (TextView) tr3.getChildAt(i);

                    TextView biggerCell   = dataCell;

                    if(biggerCell.getMeasuredWidth() < headerCell.getMeasuredWidth())
                    {
                        biggerCell = headerCell;
                    }

                    dataCell.setLayoutParams(new TableRow.LayoutParams
                    (
                            biggerCell.getMeasuredWidth(),
                            dataCell.getMeasuredHeight()
                    ));

                    headerCell.setLayoutParams(new TableRow.LayoutParams
                            (
                                    biggerCell.getMeasuredWidth(),
                                    heightH
                            ));

                    footerCell.setLayoutParams(new TableRow.LayoutParams
                            (
                                    biggerCell.getMeasuredWidth(),
                                    heightH
                            ));

                }
            }
        });
    }

    private String getRows(String string, int columns)
    {
        int rows = string.length() / columns;
        int lastString = string.length() % columns;
        String newString = "";

        for(int row = 0; row < rows; row++)
        {
            newString+=string.substring(row*columns,row*columns+columns) + ((row < rows - 1) ? "\n" : "");
        }

        newString = newString + (lastString == 0 ? "" : ("\n" + string.substring(rows * columns,rows * columns + lastString)));

        return newString;
    }

    private void addHeaderRowToTable(TableRow tr_head,TableLayout table)
    {
        table.addView(tr_head, new TableLayout.LayoutParams
        (
                Gravity.CENTER_HORIZONTAL,
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
        );
    }

    private void addDataRowToTable(TableRow tr_head,TableLayout table) {
        table.addView(tr_head, new TableLayout.LayoutParams
                (
                        Gravity.CENTER_HORIZONTAL,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT)
        );
    }
    private void addCenterWithBorderColumnToRow(TableRow tr_head, String text, int uid, int textColor, int padding, int backGroundDrawableForBorder)
    {
        TextView label = new TextView(this);

        label.setBackgroundResource(backGroundDrawableForBorder);
        label.setGravity(Gravity.CENTER);
        label.setId(uid);
        label.setText(text);
        label.setTextColor(textColor);
        label.setPadding(padding, padding, padding, padding);

        tr_head.addView(label);// add the column to the table row here
    }
    private void addLeft0dpColumnToRow(TableRow tr_head, String text, int uid, int textColor, int backColor, int padding)
    {
        TextView label = new TextView(this);
        label.setGravity(Gravity.START);
        label.setId(uid);
        label.setText(text);
        label.setWidth(0);
        label.setTextColor(textColor);
        label.setBackgroundColor(backColor);
        label.setPadding(padding, padding, padding, padding);
        tr_head.addView(label);// add the column to the table row here
    }
    private void addCurrencyRight0dpColumnToRow(TableRow tr_head, String text, int uid, int textColor, int backColor, int padding)
    {
        TextView label = new TextView(this);
        label.setGravity(Gravity.END);
        label.setId(uid);
        label.setWidth(0);
        label.setText(MagazzinoService.formatCurrency(text,MagazzinoService.getLocale(this)));
        label.setTextColor(textColor);
        label.setBackgroundColor(backColor);
        label.setPadding(padding, padding, padding, padding);
        tr_head.addView(label);// add the column to the table row here
    }
    private void addCurrencyLeft0dpColumnToRow(TableRow tr_head, String text, int uid, int textColor, int backColor, int padding)
    {
        TextView label = new TextView(this);
        label.setGravity(Gravity.START);
        label.setId(uid);
        label.setWidth(0);
        label.setText(MagazzinoService.formatCurrency(text,MagazzinoService.getLocale(this)));
        label.setTextColor(textColor);
        label.setBackgroundColor(backColor);
        label.setPadding(padding, padding, padding, padding);
        tr_head.addView(label);// add the column to the table row here
    }
    private TableRow buildTableHeader(int uid,int backGroundColor)
    {
        TableRow tr_head = new TableRow(this);
        tr_head.setId(uid);
        tr_head.setBackgroundColor(backGroundColor);
        tr_head.setLayoutParams
                (new TableLayout.LayoutParams
        (
                Gravity.CENTER,
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        )
        );

        return tr_head;
    }
    private TableRow buildTableData(boolean isOne, int uid,int firstColor, int secondColor)
    {
        TableRow tr_data = new TableRow(this);
        tr_data.setId(uid);

        tr_data.setBackgroundColor(isOne ? firstColor : secondColor);

        tr_data.setLayoutParams(new TableLayout.LayoutParams
                (
                        Gravity.CENTER_HORIZONTAL,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT)
        );

        return tr_data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prodotti, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
        case R.id.add:

            return(true);
            default: break;
          }
        return(super.onOptionsItemSelected(item));
    }
}
