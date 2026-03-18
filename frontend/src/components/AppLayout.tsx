import { Box, Container } from "@mui/material";
import { Outlet } from "react-router-dom";
import { Header } from "./Header";

export function AppLayout() {
  return (
    <Box sx={{ minHeight: "100vh", background: "linear-gradient(180deg, #eef4f8 0%, #f8fafc 100%)" }}>
      <Header />
      <Container maxWidth="lg" sx={{ py: { xs: 3, md: 5 } }}>
        <Outlet />
      </Container>
    </Box>
  );
}
