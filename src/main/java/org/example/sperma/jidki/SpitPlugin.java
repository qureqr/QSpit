package org.example.sperma.jidki;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SpitPlugin extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Регистрация команды и исполнителя команды
        this.getCommand("spit").setExecutor(this);

        this.getCommand("fart").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Здесь можно добавить код для отключения плагина, если это необходимо
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду можно использовать только в игре!");
            return true;
        }

        Player player = (Player) sender;
        if (label.equalsIgnoreCase("spit")) {
            Vector eyeLocation = player.getEyeLocation().toVector();

            LlamaSpit llamaSpit = (LlamaSpit) player.getWorld().spawnEntity(eyeLocation.toLocation(player.getWorld()), EntityType.LLAMA_SPIT);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LLAMA_SPIT, 1.0f, 1.0f);

            Vector direction = player.getEyeLocation().getDirection();
            llamaSpit.setVelocity(direction.multiply(1.0));
            Vector particleDirection = llamaSpit.getVelocity().normalize().multiply(0.1); // Множитель определяет скорость партиклов
            player.getWorld().spawnParticle(Particle.CLOUD, llamaSpit.getLocation(), 1, particleDirection.getX(), particleDirection.getY(), particleDirection.getZ(), 1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location spitLocation = llamaSpit.getLocation();

                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (target.equals(player)) continue; // Пропустить отправителя плевка

                        Location targetLocation = target.getLocation();

                        if (spitLocation.getWorld() != targetLocation.getWorld()) continue; // Пропустить игроков в других мирах

                        double distanceSquared = spitLocation.distanceSquared(targetLocation);
                        if (distanceSquared < 2.5) {
                            target.damage(20.0);
                            target.sendMessage("В вас попал харчёк от " + player.getName() + "!");
                            player.sendMessage("Вы попали харчком в " + target.getName() + "!");
                            llamaSpit.remove();
                            cancel();
                            break;
                        }
                    }
                }
            }
            .runTaskTimer(this, 0, 1);
            
            // Оповещаем игрока о вызове плевка ламы
            player.sendMessage("харчёк полетел");
        } else if (label.equalsIgnoreCase("fart")) {
            Location playerLocation = player.getLocation();
            player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, playerLocation, 100, 0.5, 0.5, 0.5, 0.1);
            player.getWorld().playSound(playerLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            player.sendMessage("уфф пернул знатно");
        }
        return true;
    }
}