package fr.recia.restriction_rentree_api.service;

import fr.recia.restriction_rentree_api.dto.SarapisRequestClasseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SarapisQueryService {

    private final JdbcTemplate sarapisJdbcTemplate;

    private static final String QUERY_CLASSES_BY_UAI = String.join("\n",
            "SELECT DISTINCT ag.cn AS classe, m.filiere AS niveau",
            "FROM agroupe ag",
            "JOIN agroupeofapersonne agoa ON ag.id = agoa.id",
            "JOIN agroupeoffoncclassegroupe agoca ON agoa.id = agoca.id",
            "JOIN etablissement e ON agoca.etablissement_fk = e.id",
            "JOIN classe c ON agoca.id = c.id",
            "JOIN classes_mefs cm ON c.id = cm.CLASSE_ID",
            "JOIN mef m ON cm.MEF_ID = m.id",
            "JOIN apersonnes_agroupes aa ON agoa.id = aa.AGROUPEOFAPERS_ID",
            "JOIN apersonne ap ON aa.APERSONNE_ID = ap.id",
            "WHERE ap.etat != 'Delete'",
            "AND ag.categorie = 'Classe'",
            "AND e.uai = ?"
    );

    public SarapisQueryService(JdbcTemplate sarapisJdbcTemplate){
        this.sarapisJdbcTemplate = sarapisJdbcTemplate;
    }

    private final RowMapper<SarapisRequestClasseDTO> rowMapper = (rs, rowNum) -> {
        SarapisRequestClasseDTO dto = new SarapisRequestClasseDTO();
        dto.setClasse(rs.getString("classe"));
        dto.setNiveau(rs.getString("niveau"));
        return dto;
    };

    public List<SarapisRequestClasseDTO> getClasses(String uai) {
        return sarapisJdbcTemplate.query(QUERY_CLASSES_BY_UAI, rowMapper, uai);
    }
}
