import MenuBookRoundedIcon from "@mui/icons-material/MenuBookRounded";
import { AppBar, Box, Toolbar, Typography } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";

export function Header() {
  return (
    <AppBar position="sticky" elevation={0}>
      <Toolbar sx={{ minHeight: 72 }}>
        <Box
          component={RouterLink}
          to="/"
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 1.5,
            color: "inherit",
            textDecoration: "none"
          }}
        >
          <MenuBookRoundedIcon />
          <Typography variant="h6" fontWeight={700}>
            DOB Land Record Management System
          </Typography>
        </Box>
      </Toolbar>
    </AppBar>
  );
}
