package com.odazie.simpleblog.seed;

import com.odazie.simpleblog.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.odazie.simpleblog.model.Player;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadPlayerData();
    }

    private void loadPlayerData() {
        if (playerRepository.findByPlayerName("Pakallale") == null) {
            Player player1 = new Player();
            player1.setPlayerName("Pakallale");
            player1.setEncryptedAccountId("HEgGP9Vk2Y-x8htDUH5ZCX3YYwtXKN66_5S8uTgFP7BCxsI6ju1ni6_xHQ");
            player1.setPuuid("dPzq5xM3oDaqvItEVXly4SYex917RvLI7PC_u6VyD0e6cx1ktaRCtVycIYrN2RDMzQGo0nzKuuwy0A");
            player1.setActualName("Trzcina");
            player1.setMainAccountDivision("Emerald 2 40LP");
            player1.setImageLink("=IMAGE(\"https://cdn.discordapp.com/attachments/1212427418934050826/1212758027573272638/blazej.png?ex=65f2ffed&is=65e08aed&hm=e09e4309ff4adfdccfe06b595c3031042e31297065dcd7bedfa0b90ecb4c4e0b&\";4;80;80)");
            playerRepository.save(player1);
        }

        if (playerRepository.findByPlayerName("Nrynather") == null) {
            Player player2 = new Player();
            player2.setPlayerName("Nrynather");
            player2.setEncryptedAccountId("9LBP-pLpGdcWWxYuDp87sFBKdfpg74DpzFz-DNUoVhmGPujaxtPcXeWVMA");
            player2.setPuuid("VAxS2fxh1f8fsPINnVzjRLUmzXPWEje0llwvEi22Vi15zJCzJeERTgxQnYc88Qlj22DGMBPsERA9_w");
            player2.setActualName("Konrad");
            player2.setMainAccountDivision("Emerald 2 0LP");
            player2.setImageLink("=IMAGE(\"https://cdn.discordapp.com/attachments/1212427418934050826/1212758028604932116/krzywy.png?ex=65f2ffed&is=65e08aed&hm=512cf67f248822566db935662334fe0fcb026c7d21b8944bff24827e389cc844&\";4;80;80)");
            playerRepository.save(player2);
        }

        if(playerRepository.findByPlayerName("Elimirailin") == null) {
            Player player3 = new Player();
            player3.setPlayerName("Elimirailin");
            player3.setEncryptedAccountId("4dYF_uwM8tfjq-JXbAzd_YQsjpkgga7RWbLfStoLh8_9gUFnufK-JNkPMA");
            player3.setPuuid("u5xafKA-VOIqEIbOAPgi02TK7JIQVnBCpO_aS3rrtJwY3rDTxJ_fZblWnzmZPgHXuwdYxK-FOTfmLw");
            player3.setActualName("Trajkowski");
            player3.setMainAccountDivision("Master Tier 40LP");
            player3.setImageLink("=IMAGE(\"https://cdn.discordapp.com/attachments/1212427418934050826/1212758028089303081/trajek.png?ex=65f2ffed&is=65e08aed&hm=92b1d8d17d03b3aa7f7dc3748161f16c6bd3e06b03cffa59c9c16d7f30376f13&\";4;80;80)");
            playerRepository.save(player3);
        }

        if(playerRepository.findByPlayerName("Nettemancela") == null) {
            Player player4 = new Player();
            player4.setPlayerName("Nettemancela");
            player4.setEncryptedAccountId("DljQ7elldWmY95Np26FVfK7rYR9WZxON4Nj2XBrZP9dJuiG_NnBzBiKmBg");
            player4.setPuuid("Rlvrrez9FrH8dcjSPOlS-0UWTOilmut3K3t3RqcOkIM7ClAniaHWt6M9mRkLWijByKUyy07UbGHucg");
            player4.setActualName("Cwik");
            player4.setMainAccountDivision("Gold 1    40LP");
            player4.setImageLink("=IMAGE(\"https://cdn.discordapp.com/attachments/1212427418934050826/1212805272142086204/cwik.png?ex=65f32bed&is=65e0b6ed&hm=214280e287366265bb67c4563b02768e0d199f58b0ab1758db137a40e005a3db&\";4;80;80)");
            playerRepository.save(player4);
        }
    }
}