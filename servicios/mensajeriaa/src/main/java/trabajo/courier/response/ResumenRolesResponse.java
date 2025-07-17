package trabajo.courier.response;

import java.util.List;

import trabajo.courier.DTO.RolUsuarioDTO;

    public class ResumenRolesResponse {
        private long totalRoles;
        private List<RolUsuarioDTO> roles;

        public ResumenRolesResponse(long totalRoles, List<RolUsuarioDTO> roles) {
            this.totalRoles = totalRoles;
            this.roles = roles;
        }

        public long getTotalRoles() { return totalRoles; }
        public void setTotalRoles(long totalRoles) { this.totalRoles = totalRoles; }

        public List<RolUsuarioDTO> getRoles() { return roles; }
        public void setRoles(List<RolUsuarioDTO> roles) { this.roles = roles; }
    }