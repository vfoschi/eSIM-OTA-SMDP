import React from "react";
import Typography from "@material-ui/core/Typography";
import CircularProgress from "@material-ui/core/CircularProgress";
import { DataGrid, RowsProp, ColDef } from "@material-ui/data-grid";
import { Grid } from "@material-ui/core";
import { useGetTableAPI, useGetColumnsAPI } from "../../utils";
import AddUser from "./adduser";
import DeleteUser from "./deleteuser";
import UpdateUser from "./updateuser";
import QueueUpdate from "./queueupdate";
import LocationActive from './locationactive';

export default function Users() {
  const [users, isLoading, isError] = useGetTableAPI(`api`, "users");
  const [cols, isColsLoading, isColsError] = useGetColumnsAPI(`api`, "users");

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
            All Users: <br />
          </Typography>
        </Grid>
      </Grid>
      <Grid container justify="flex-end">
        <Grid item>
          <AddUser />
        </Grid>
        <Grid item>
          <UpdateUser />
        </Grid>
        <Grid item>
          <QueueUpdate />
        </Grid>
        <Grid item>
          <LocationActive />
        </Grid>
        <Grid item>
          <DeleteUser />
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
