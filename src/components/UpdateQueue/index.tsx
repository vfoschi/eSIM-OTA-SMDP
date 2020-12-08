import React from "react";
import Typography from "@material-ui/core/Typography";
import CircularProgress from "@material-ui/core/CircularProgress";
import { DataGrid, RowsProp, ColDef } from "@material-ui/data-grid";
import { Grid } from "@material-ui/core";
import { useGetTableAPI, useGetColumnsAPI } from "../../utils";

export default function UpdateQueue() {
  const [users, isLoading, isError] = useGetTableAPI(
    "updatequeue"
  );
  const [cols, isColsLoading, isColsError] = useGetColumnsAPI("updatequeue");

  if (isColsLoading || isLoading) {
    return <CircularProgress />;
  }
  if (isColsError || isError) {
    return (
      <div>
        An error happened. Checked logs. <br />{" "}
      </div>
    );
  }
  const columns: ColDef[] = cols.map((col, index) => ({
    field: "col" + index,
    headerName: col,
    width: 200,
  }));

  const rows: RowsProp = users.map((user, index) => {
    let rowObj: { [colName: string]: string; id: string } = { id: index + "" };
    for (let colIndex in cols) {
      const colName = "col" + colIndex;
      rowObj[colName] = user[colIndex];
    }
    return rowObj;
  });
  return (
    <div>
      <Grid container>
        <Grid item xs={12}>
          <Typography variant="body1">
            Update Queue for users: <br />
          </Typography>
        </Grid>
      </Grid>
      <Grid container>
        <Grid item xs={12}>
          <div style={{ height: 800, width: "100%" }}>
            <DataGrid rows={rows} columns={columns} />
          </div>
        </Grid>
      </Grid>
    </div>
  );
}
