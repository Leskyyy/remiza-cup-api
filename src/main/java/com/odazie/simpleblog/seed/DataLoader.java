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
            playerRepository.save(player1);
        }

        if (playerRepository.findByPlayerName("Nrynather") == null) {
            Player player2 = new Player();
            player2.setPlayerName("Nrynather");
            player2.setEncryptedAccountId("9LBP-pLpGdcWWxYuDp87sFBKdfpg74DpzFz-DNUoVhmGPujaxtPcXeWVMA");
            player2.setPuuid("VAxS2fxh1f8fsPINnVzjRLUmzXPWEje0llwvEi22Vi15zJCzJeERTgxQnYc88Qlj22DGMBPsERA9_w");
            playerRepository.save(player2);
        }

        if(playerRepository.findByPlayerName("Elimirailin") == null) {
            Player player3 = new Player();
            player3.setPlayerName("Elimirailin");
            player3.setEncryptedAccountId("4dYF_uwM8tfjq-JXbAzd_YQsjpkgga7RWbLfStoLh8_9gUFnufK-JNkPMA");
            player3.setPuuid("u5xafKA-VOIqEIbOAPgi02TK7JIQVnBCpO_aS3rrtJwY3rDTxJ_fZblWnzmZPgHXuwdYxK-FOTfmLw");
            playerRepository.save(player3);
        }

        if(playerRepository.findByPlayerName("Nettemancela") == null) {
            Player player4 = new Player();
            player4.setPlayerName("Nettemancela");
            player4.setEncryptedAccountId("DljQ7elldWmY95Np26FVfK7rYR9WZxON4Nj2XBrZP9dJuiG_NnBzBiKmBg");
            player4.setPuuid("Rlvrrez9FrH8dcjSPOlS-0UWTOilmut3K3t3RqcOkIM7ClAniaHWt6M9mRkLWijByKUyy07UbGHucg");
            playerRepository.save(player4);
        }
    }
}